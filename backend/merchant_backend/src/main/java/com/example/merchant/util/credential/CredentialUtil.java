package com.example.merchant.util.credential;

import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.payment.error.InvalidMerchantTypeException;
import com.example.merchant.global.enums.MerchantType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CredentialUtil {

    @Value("${pos.merchant.api.key}")
    private String POS_MER_API_KEY; // POS -> MER
    @Value("${mobi.merchant.api.key}")
    private String MOBI_MER_API_KEY; // MOBI -> MER

    @Value("${parking.merchant.id}")
    private Long PARKING_MER_ID;
    @Value("${oil.merchant.id}")
    private Long OIL_MER_ID;
    @Value("${food.merchant.id}")
    private Long FOOD_MER_ID;
    @Value("${washing.merchant.id}")
    private Long WASHING_MER_ID;
    @Value("${motel.merchant.id}")
    private Long MOTEL_MER_ID;
    @Value("${street.merchant.id}")
    private Long STREET_MER_ID;

    @Value("${parking.mobi.api.key}")
    private String PARKING_MOBI_API_KEY; // PARKING -> MOBI
    @Value("${oil.mobi.api.key}")
    private String OIL_MOBI_API_KEY; // OIL -> MOBI
    @Value("${food.mobi.api.key}")
    private String FOOD_MOBI_API_KEY; // FOOD -> MOBI
    @Value("${washing.mobi.api.key}")
    private String WASHING_MOBI_API_KEY; // WASHING -> MOBI
    @Value("${motel.mobi.api.key}")
    private String MOTEL_MOBI_API_KEY; // MOTEL -> MOBI
    @Value("${street.mobi.api.key}")
    private String STREET_MOBI_API_KEY; // STREET -> MOBI

    public void validatePosMerApiKey(String merApiKey) {
        if (POS_MER_API_KEY.equals(merApiKey)) {
            return;
        }
        throw new InvalidMerApiKeyException();
    }

    public Long getMerchantIdByType(MerchantType merchantType) {
        return switch (merchantType) {
            case PARKING -> PARKING_MER_ID;
            case OIL -> OIL_MER_ID;
            case FOOD -> FOOD_MER_ID;
            case WASHING -> WASHING_MER_ID;
            case MOTEL -> MOTEL_MER_ID;
            case STREET -> STREET_MER_ID;
            default -> throw new InvalidMerchantTypeException();
        };
    }

    public String getMobiApiKeyByType(MerchantType merchantType) {
        return switch (merchantType) {
            case PARKING -> PARKING_MOBI_API_KEY;
            case OIL -> OIL_MOBI_API_KEY;
            case FOOD -> FOOD_MOBI_API_KEY;
            case WASHING -> WASHING_MOBI_API_KEY;
            case MOTEL -> MOTEL_MOBI_API_KEY;
            case STREET -> STREET_MOBI_API_KEY;
            default -> throw new InvalidMerchantTypeException();
        };
    }
}
