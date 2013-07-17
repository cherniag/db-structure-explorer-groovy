package mobi.nowtechnologies.server.trackrepo.utils;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if(value==null)return;

        if(!value.getClass().getName().startsWith("java.lang")){
            try {
                Object oldval = getPropertyUtils().getProperty(dest, name);
                if(value instanceof Collection){
                    Iterator<?> i = ((Collection) oldval).iterator();
                    for (Object obj : (Collection)value) {
                        if(i != null && i.hasNext()){
                            Object oldobj =  i.next();
                            super.copyProperties(oldobj, obj);
                        }
                    }
                } else {
                    super.copyProperties(oldval, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        super.copyProperty(dest, name, value);
    }

}
