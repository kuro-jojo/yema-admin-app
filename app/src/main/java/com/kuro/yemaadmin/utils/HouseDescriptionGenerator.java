package com.kuro.yemaadmin.utils;

import static androidx.core.content.ContextCompat.getString;

import android.content.Context;

import com.kuro.yemaadmin.R;
import com.kuro.yemaadmin.data.enums.HouseType;
import com.kuro.yemaadmin.data.model.House;

public class HouseDescriptionGenerator {

    public static String generateDescription(House house, Context context) {
        StringBuilder description = new StringBuilder();

        String typeHeader = "";
        String typeDescription = "";
        String areaUnit = "m²";

        switch (house.getHouseType()) {
            case DUPLEX:
                typeHeader = getString(context, R.string.duplex_type_header);
                typeDescription = getString(context, R.string.duplex_type_description);
                break;

            case STUDIO:
                typeHeader = getString(context, R.string.studio_type_header);
                typeDescription = getString(context, R.string.studio_type_description);
                break;

            case APARTMENT:
                typeHeader = getString(context, R.string.apartment_type_header);
                typeDescription = getString(context, R.string.apartment_type_description);
                break;

            case FLAT:
                typeHeader = getString(context, R.string.flat_type_header);
                typeDescription = getString(context, R.string.flat_type_description);
                break;

            case VILLA:
                typeHeader = getString(context, R.string.villa_type_header);
                typeDescription = getString(context, R.string.villa_type_description);
                break;

            case SEMI_DETACHED:
                typeHeader = getString(context, R.string.semi_detached_type_header);
                typeDescription = getString(context, R.string.semi_detached_type_description);
                break;

            default:
                typeHeader = getString(context, R.string.default_type_header);
                typeDescription = getString(context, R.string.default_type_description);
        }

        description.append("<h3>").append(typeHeader).append("</h3>");
        description.append(String.format(typeDescription,
                house.getNumberBedrooms(), house.getNumberBathrooms(), house.getLivingArea(), areaUnit));

        description.append(String.format(getString(context, R.string.opportunity_message),
                getString(context, (house.getHouseType() == HouseType.STUDIO) ? R.string.minimalist_lifestyle : R.string.make_home_message))).append("<br><br>");
        description.append("<b>").append(getString(context, R.string.contact_information)).append("</b>");

        return description.toString();
    }
}
