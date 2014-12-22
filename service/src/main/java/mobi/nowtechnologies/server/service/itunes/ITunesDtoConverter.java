package mobi.nowtechnologies.server.service.itunes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionResponseDto;
import mobi.nowtechnologies.server.shared.service.BasicResponse;

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/10/2014
 */
public class ITunesDtoConverter {
    private JsonParser jsonParser = new JsonParser();
    private Gson gson = new Gson();

    public ITunesInAppSubscriptionResponseDto convertToResponseDTO(BasicResponse basicResponse) {
        ITunesInAppSubscriptionResponseDto iTunesResponseDTO =  new ITunesInAppSubscriptionResponseDto();
        JsonObject rootObject = jsonParser.parse(basicResponse.getMessage()).getAsJsonObject();
        iTunesResponseDTO.setStatus(getStringFromJsonObject(rootObject, "status"));
        iTunesResponseDTO.setLatestReceipt(getStringFromJsonObject(rootObject, "latest_receipt"));
        iTunesResponseDTO.setReceipt(getObjectFromJsonObject(rootObject, "receipt", ITunesInAppSubscriptionResponseDto.Receipt.class));
        fillLatestReceiptInfo(rootObject, iTunesResponseDTO);
        return iTunesResponseDTO;
    }

    public String convertToRequestBody(String appStoreReceipt, String password){
        ITunesInAppSubscriptionRequestDto requestDto = new ITunesInAppSubscriptionRequestDto(appStoreReceipt, password);
        return gson.toJson(requestDto);
    }

    private String getStringFromJsonObject(JsonObject rootObject, String name) {
        JsonElement jsonElement = rootObject.get(name);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    private <T> T getObjectFromJsonObject(JsonObject rootObject, String name, Class<T> clazz){
        JsonElement jsonElement = rootObject.get(name);
        return jsonElement != null ? gson.fromJson(jsonElement, clazz) : null;
    }

    private void fillLatestReceiptInfo(JsonObject rootObject, ITunesInAppSubscriptionResponseDto iTunesResponseDTO) {
        if (!rootObject.has("latest_receipt_info")){
            throw new IllegalArgumentException(String.format("Json object [%s] doesn't contain latest_receipt_info", rootObject));
        }
        JsonElement latestReceiptInfo = rootObject.get("latest_receipt_info");
        if(latestReceiptInfo.isJsonArray()){
            if (latestReceiptInfo.getAsJsonArray().size() != 1){
                throw new IllegalArgumentException(String.format("latest_receipt_info [%s] have a wrong size. Must be 1", latestReceiptInfo));
            }
            iTunesResponseDTO.setLatestReceiptInfo(gson.fromJson(latestReceiptInfo.getAsJsonArray().get(0), ITunesInAppSubscriptionResponseDto.Receipt.class));
        } else if(latestReceiptInfo.isJsonObject()){
            iTunesResponseDTO.setLatestReceiptInfo(gson.fromJson(latestReceiptInfo, ITunesInAppSubscriptionResponseDto.Receipt.class));
        } else {
            throw new IllegalArgumentException(String.format("latest_receipt_info [%s] is neither array nor object", latestReceiptInfo));
        }
    }
}
