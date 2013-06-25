package mobi.nowtechnologies.validator;

import mobi.nowtechnologies.domain.Track;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class TrackValidator implements Validator {
	public boolean supports(Class candidate) {
		return Track.class.isAssignableFrom(candidate);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title",
				"errore.campoRichiesto", new String[] { "title" }, "required");
		ValidationUtils
				.rejectIfEmptyOrWhitespace(errors, "artist",
						"errore.campoRichiesto", new String[] { "artist" },
						"required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ISRC",
				"errore.campoRichiesto", new String[] { "ISRC" }, "required");
	}
}
