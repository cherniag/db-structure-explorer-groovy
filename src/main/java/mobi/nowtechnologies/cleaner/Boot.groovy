package mobi.nowtechnologies.cleaner

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

/**
 * Author: Gennadii Cherniaiev
 * Date: 6/3/2015
 */
class Boot {

    /*
    -Djdbc.userName=root -Djdbc.password=12345 -Djdbc.host=localhost -Djdbc.port=3306 -Daction=DELETE|SELECT|COUNT -Did=<id>
     */
    static def resultQueries = [:]

    public static void main(String[] args) {
        Sql informationSchemaSql = initializeSql('information_schema')

        def foreignConstraints = [] as ArrayList
        def rootFK = new FK(tableSchema: 'cn_service', tableName: 'tb_users', columnName: 'i')

        // import from db
        importFKConstraints(informationSchemaSql, foreignConstraints)
        importByColumnName(informationSchemaSql, foreignConstraints)

        // analyze
        fetchChildsRecursevely(foreignConstraints, rootFK)
        findCyclicReferences(rootFK, rootFK)

        // printFK(rootFK, 0, '')

        buildQueries(rootFK, [])

        printResult()


    }

    private static Sql initializeSql(schema) {
        def userName = System.getProperty("jdbc.userName")
        def password = System.getProperty("jdbc.password")
        def host = System.getProperty("jdbc.host")
        def port = System.getProperty("jdbc.port")

        assert userName
        assert password
        assert host
        assert port

        Sql.newInstance("jdbc:mysql://$host:$port/$schema?useUnicode=yes&amp;characterEncoding=UTF-8".toString(),
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
            foreignConstraints.each {
                if (it.tableSchema == fk.tableSchema && it.tableName == fk.tableName && it.columnName == fk.columnName) contains = true
            }
            if (!contains) foreignConstraints << fk
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

        def action = System.getProperty("action")
        switch (action) {
            case 'DELETE': fk.cyclicChilds.each {
                resultQueries[(it)] = "update `${it.tableName}` " +
                        "set `${it.tableName}`.${it.columnName} = NULL where ${it.backReference.referencedTableName}.${it.backReference.referencedColumnName} = "
            }; break

            case 'INSERT': break ;

            case 'SELECT': fk.cyclicChilds.each {
                resultQueries[(it)] = "select '(C)',`${it.tableName}`.${it.columnName}, `${it.referencedTableName}`.${it.referencedColumnName}  " +
                        "from `${it.tableName}` join ${it.referencedTableName} on `${it.tableName}`.${it.columnName} = ${it.referencedTableName}.${it.referencedColumnName} " +
                        "where `${it.backReference.referencedTableName}`.${it.backReference.referencedColumnName} = "
            }; break

            case 'COUNT': fk.cyclicChilds.each {
                resultQueries[(it)] = "select count(*) " +
                        "from `${it.tableName}` join ${it.referencedTableName} on `${it.tableName}`.${it.columnName} = ${it.referencedTableName}.${it.referencedColumnName} "
            }; break

            default: throw new IllegalArgumentException("Action $action is wrong, -Daction=DELETE|SELECT|COUNT")
        }


        buildDelete(list, action)

    }

    private static void printResult() {
        def action = System.getProperty("action")
        def id = System.getProperty("id")
        Sql dataSql = initializeSql('cn_service')
        Sql SqlTo = Sql.newInstance("jdbc:mysql://localhost:3306/cn_service?useUnicode=yes&amp;characterEncoding=UTF-8", 'root', '12345', "com.mysql.jdbc.Driver")

        resultQueries.entrySet().each { query ->
            switch (action) {
                case 'DELETE':
                case 'SELECT': println query.value.toString() + id + ";"; break
                case 'COUNT': println query.value.toString() + " where tb_users.userGroup < 10;"; break
                case 'INSERT':

                    def rows = dataSql.rows(query.value.toString() + id)
                    rows.each { row ->
                        def count = SqlTo.execute(generateInsert(query.key.tableName, row.keySet().size()), row.values().asList())
                        println query.key.tableName + " : " + count
                    } ; break


                default: throw new IllegalArgumentException("Action $action is wrong, -Daction=DELETE|SELECT|COUNT|INSERT")
            }
        }
    }

    private static void buildDelete(ArrayList list, action) {
        def size = list.size()
        StringBuilder sb = new StringBuilder("`").append(list.get(size - 1).tableName).append("`")
        for (int i = size - 1; i > 0; i--) {
            sb.append(" join `")
            sb.append(list.get(i).referencedTableName)
            sb.append("` on `")
            sb.append(list.get(i).tableName)
            sb.append("`.")
            sb.append(list.get(i).columnName)
            sb.append(" = `")
            sb.append(list.get(i).referencedTableName)
            sb.append("`.")
            sb.append(list.get(i).referencedColumnName)
        }

        switch (action) {
            case 'DELETE': resultQueries[list.get(size - 1)] = "delete `${list.get(size - 1).tableName}` from $sb where tb_users.i = "; break
            case 'INSERT':
            case 'SELECT': resultQueries[list.get(size - 1)] = "select `${list.get(size - 1).tableName}`.* from $sb where tb_users.i = "; break
            case 'COUNT': resultQueries[list.get(size - 1)] = "select count(*) from $sb"; break
            default: throw new IllegalArgumentException("Action $action is wrong, -Daction=DELETE|SELECT|COUNT")
        }
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

    static def generateInsert(tableName, int paramsCount) {
        def l = []
        paramsCount.times { l << '?' }
        "insert into $tableName values (${l.join(',')})"
    }
}
