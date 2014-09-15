package mobi.nowtechnologies.applicationtests.features.activation.facebook

import mobi.nowtechnologies.server.shared.enums.ActivationStatus

/**
 * @author kots
 * @since 8/14/2014.
 */
class UserState {
    String status
    String paymentType
    Boolean fullyRegistred
    Boolean freeTrial
    ActivationStatus activation
    String provider
    Boolean hasAllDetails
}
