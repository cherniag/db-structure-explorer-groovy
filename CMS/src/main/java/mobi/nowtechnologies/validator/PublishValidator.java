package mobi.nowtechnologies.validator;

import mobi.nowtechnologies.mvc.controller.PublishData;
import mobi.nowtechnologies.util.Property;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PublishValidator implements Validator {
	public boolean supports(Class candidate) {
		return PublishData.class.isAssignableFrom(candidate);
	}

	public void validate(Object obj, Errors errors) {
		/*ValidationUtils
				.rejectIfEmptyOrWhitespace(errors, "artist.info",
						"error.info.empty", new String[] { "Info" },
						"required");*/
		PublishData data = (PublishData) obj;
		System.out.println("Got info "+data.getArtist().getInfo());
		if (data.getArtist().getInfo().length() > Property.getInstance().getLongValue("cn.info.maxlength")) {
			errors.rejectValue("artist.info", "error.info.toolong", "Too long. Max size is "+Property.getInstance().getLongValue("cn.info.maxlength"));
		}
		if (data.getArtist().getInfo().contains("&")) {
			errors.rejectValue("artist.info", "error.info.invalid", "invalid character &");
		}
		if (data.getPublishArtist().length() > Property.getInstance().getLongValue("cn.artist.maxlength")) {
			errors.rejectValue("artist.name", "error.artist.toolong", "Too long. May size is "+Property.getInstance().getLongValue("cn.artist.maxlength"));
		}
		if (data.getPublishArtist().contains("&")) {
			errors.rejectValue("artist.name", "error.name.invalid", "invalid character &");
		}
		if (data.getPublishTitle().length() > Property.getInstance().getLongValue("cn.title.maxlength")) {
			errors.rejectValue("track.title", "error.title.toolong", "Too long. May size is "+Property.getInstance().getLongValue("cn.title.maxlength"));
		}
		if (data.getPublishTitle().contains("&")) {
			errors.rejectValue("artist.title", "error.title.invalid", "invalid character &");
		}


		
	}
}
