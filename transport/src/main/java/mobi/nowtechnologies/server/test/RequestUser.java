/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.test;

/**
 * Created by zam on 4/2/2015.
 */
public @interface RequestUser {

    /**
     * Whether the attribute is required. <p>Default is {@code true}, leading to an exception thrown in case of the attribute missing in the request. Switch this to {@code false} if you prefer a
     * {@code null} in case of the attribute missing.
     */
    boolean required() default true;
}
