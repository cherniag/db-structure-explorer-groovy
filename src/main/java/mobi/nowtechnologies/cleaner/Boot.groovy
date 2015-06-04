package mobi.nowtechnologies.cleaner

import groovy.sql.Sql

/**
 * Author: Gennadii Cherniaiev
 * Date: 6/3/2015
 */
class Boot {

    /*
    -Djdbc.userName=root -Djdbc.password=12345 -Djdbc.host=localhost -Djdbc.port=3306
     */

    public static void main(String[] args) {
        Sql informationSchemaSql = initializeSql()

        def foreignConstraints = [] as ArrayList
        def rootFK = new FK(tableSchema:'cn_service',tableName: 'tb_users',columnName: 'i')

        // import from db
        importFKConstraints(informationSchemaSql, foreignConstraints)
        importByColumnName(informationSchemaSql, foreignConstraints)

        // analyze
        fetchChildsRecursevely(foreignConstraints, rootFK)
        findCyclicReferences(rootFK, rootFK)

        // printFK(rootFK, 0, '')

        buildQueries(rootFK, [])
    }

    private static Sql initializeSql() {
        def userName = System.getProperty("jdbc.userName")
        def password = System.getProperty("jdbc.password")
        def host = System.getProperty("jdbc.host")
        def port = System.getProperty("jdbc.port")

        assert userName
        assert password
        assert host
        assert port

        Sql.newInstance("jdbc:mysql://$host:$port/information_schema?useUnicode=yes&amp;characterEncoding=UTF-8".toString(),
                userName, password, "com.mysql.jdbc.Driver")
    }

    private static importByColumnName(Sql informationSchemaSql, foreignConstraints) {
        informationSchemaSql.eachRow(
                """
                select c.*
                from COLUMNS c
                where c.table_schema='cn_service' and lower(c.column_name) in ('userid', 'user_id', 'owner_id', 'ownerid', 'useruid')
            """) {

            def fk = new FK()
            fk.constraintSchema = it.TABLE_SCHEMA
            fk.tableSchema = it.TABLE_SCHEMA
            fk.tableName = it.TABLE_NAME
            fk.columnName = it.COLUMN_NAME
            fk.referencedTableSchema = 'cn_service'
            fk.referencedTableName = 'tb_users'
            fk.referencedColumnName = 'i'

            def contains = false
            foreignConstraints.each{
                if(it.tableSchema == fk.tableSchema && it.tableName == fk.tableName && it.columnName == fk.columnName) contains = true
            }
            if(!contains) foreignConstraints << fk
        }
    }

    private static importFKConstraints(Sql informationSchemaSql, foreignConstraints) {
        informationSchemaSql.eachRow(
                """
                select CONSTRAINT_SCHEMA, CONSTRAINT_NAME, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
                from information_schema.KEY_COLUMN_USAGE
                where
                CONSTRAINT_SCHEMA='cn_service' and
                REFERENCED_TABLE_NAME is not null
            """) {

            def fk = new FK()
            fk.constraintSchema = it.CONSTRAINT_SCHEMA
            fk.constraintName = it.CONSTRAINT_NAME
            fk.tableSchema = it.TABLE_SCHEMA
            fk.tableName = it.TABLE_NAME
            fk.columnName = it.COLUMN_NAME
            fk.referencedTableSchema = it.REFERENCED_TABLE_SCHEMA
            fk.referencedTableName = it.REFERENCED_TABLE_NAME
            fk.referencedColumnName = it.REFERENCED_COLUMN_NAME

            foreignConstraints << fk
        }
    }

    private static void buildQueries(FK fk, List<FK> path) {
        def list = new ArrayList(path)
        list << fk
        fk.childs.each {
            buildQueries(it, list)
        }

        fk.cyclicChilds.each {
            println "update ${it.tableName} " +
                    "set ${it.tableName}.${it.columnName} = NULL where ${it.backReference.referencedTableName}.${it.backReference.referencedColumnName} = "
        }

        buildDelete(list)
    }

    private static void buildDelete(ArrayList list) {
        def size = list.size()
        StringBuilder sb = new StringBuilder(list.get(size - 1).tableName)
        for (int i = size - 1; i > 0; i--) {
            sb.append(" join ")
            sb.append(list.get(i).referencedTableName)
            sb.append(" on ")
            sb.append(list.get(i).tableName)
            sb.append(".")
            sb.append(list.get(i).columnName)
            sb.append(" = ")
            sb.append(list.get(i).referencedTableName)
            sb.append(".")
            sb.append(list.get(i).referencedColumnName)
        }

        println "delete ${list.get(size - 1).tableName} from $sb where tb_users.i = "
    }

    private static void printFK(FK fk, int level, String prefix) {
        level.times { print '\t' }
        if (level > 0) println '|'
        level.times { print '\t' }
        if (level > 0) print '+ - '
        print prefix
        println fk
        fk.childs.each {
            printFK(it, level + 1, '')
        }

        fk.cyclicChilds.each {
            printFK(it, level + 1, ' (C) ')
        }

    }

    private static void findCyclicReferences(FK parentFK, FK currentFK) {
        currentFK.childs.each {
            if (it.isTheSameTable(parentFK)) {
                //println "$it = $parentFK = $currentFK"
                it.backReference = currentFK
                currentFK.cyclicChilds << it
            }
            findCyclicReferences(parentFK, it)
        }

        currentFK.childs.removeAll(currentFK.cyclicChilds)
    }

    private static void fetchChildsRecursevely(ArrayList fetchFrom, FK rootFK) {
        fetchFrom.each {
            if (rootFK.isReferToMe(it)) {
                rootFK.childs << it
            }
        }

        fetchFrom.removeAll(rootFK.childs)

        rootFK.childs.each {
            fetchChildsRecursevely(fetchFrom, it)
        }
    }


    static class FK {
        String constraintSchema, constraintName, tableSchema, tableName, columnName, referencedTableSchema, referencedTableName, referencedColumnName
        def childs = [] as ArrayList<FK>
        def cyclicChilds = [] as ArrayList<FK>
        FK backReference

        def isReferToMe(FK fk) {
            fk.referencedTableSchema == tableSchema && fk.referencedTableName == tableName //&& fk.referencedColumnName == columnName
        }

        def isTheSameTable(FK fk) {
            fk.tableSchema == tableSchema && fk.tableName == tableName
        }

        @Override
        public String toString() {
            return "${tableSchema}@${tableName}.${columnName}->${referencedTableSchema}@${referencedTableName}.${referencedColumnName} (${childs.size()})".toString()
        }
    }
}
