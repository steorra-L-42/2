package com.example.merchant.domain.menu.service;

import com.example.merchant.domain.menu.dto.MenuListRequest;
import com.example.merchant.domain.menu.dto.MobiMenuListRequest;
import com.example.merchant.domain.payment.error.InvalidMerchantTypeException;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MenuService {

    private final MobiPay mobiPay;
    private final CredentialUtil credentialUtil;

    public ResponseEntity<?> getMenuList(String merApiKey, String merchantTypeStr, MenuListRequest menuListRequest) {

        credentialUtil.validatePosMerApiKey(merApiKey);
        validateMerchantType(merchantTypeStr);

        MerchantType merchantType = credentialUtil.getMerchantTypeLowerCaseString(merchantTypeStr);
        MobiMenuListRequest mobiMenuListRequest = MobiMenuListRequest.of(menuListRequest, credentialUtil.getMerchantIdByType(merchantType));

        ResponseEntity<?> mobiPayResponseEntity = mobiPay.sendMenuList(merchantType, mobiMenuListRequest);

        return ResponseEntity.status(mobiPayResponseEntity.getStatusCode()).build();
    }

    private void validateMerchantType(String merchantType) {
        if (merchantType == null || merchantType.isEmpty()) {
            throw new InvalidMerchantTypeException();
        }

        List<String> merchantTypes = List.of("parking", "oil", "food", "washing", "motel", "street");
        if(!merchantTypes.contains(merchantType)) {
            throw new InvalidMerchantTypeException();
        }
    }

}
