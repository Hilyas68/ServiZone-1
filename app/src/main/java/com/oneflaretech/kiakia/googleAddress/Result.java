package com.oneflaretech.kiakia.googleAddress;

import java.util.ArrayList;

public class Result
{
    private ArrayList<AddressComponent> address_components;

    public ArrayList<AddressComponent> getAddressComponents() { return this.address_components; }

    public void setAddressComponents(ArrayList<AddressComponent> address_components) { this.address_components = address_components; }

    private String formatted_address;

    public String getFormattedAddress() { return this.formatted_address; }

    public void setFormattedAddress(String formatted_address) { this.formatted_address = formatted_address; }

    private Geometry geometry;

    public Geometry getGeometry() { return this.geometry; }

    public void setGeometry(Geometry geometry) { this.geometry = geometry; }

    private String place_id;

    public String getPlaceId() { return this.place_id; }

    public void setPlaceId(String place_id) { this.place_id = place_id; }

    private PlusCode plus_code;

    public PlusCode getPlusCode() { return this.plus_code; }

    public void setPlusCode(PlusCode plus_code) { this.plus_code = plus_code; }

    private ArrayList<String> types;

    public ArrayList<String> getTypes() { return this.types; }

    public void setTypes(ArrayList<String> types) { this.types = types; }

    @Override
    public String toString() {
        return "Result{" +
                "address_components=" + address_components +
                ", formatted_address='" + formatted_address + '\'' +
                ", geometry=" + geometry +
                ", place_id='" + place_id + '\'' +
                ", plus_code=" + plus_code +
                ", types=" + types +
                '}';
    }
}
