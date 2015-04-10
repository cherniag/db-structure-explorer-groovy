/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.shared.message;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

// @author Titov Mykhaylo (titov) on 10.04.2015.
public class DefaultMasterPasswordEnvironmentStringPBEConfig extends EnvironmentStringPBEConfig{

    @Override
    public void setPasswordSysPropertyName(final String passwordSysPropertyName) {
        final String property = System.getProperty(passwordSysPropertyName);
        if (isNull(passwordSysPropertyName) || isNull(property)) {
            super.setPassword("secret");
        } else {
            super.setPasswordSysPropertyName(passwordSysPropertyName);
        }
    }
}
