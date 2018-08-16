package com.fincoapps.servizone.models.googleAddress;

import java.util.ArrayList;

public class MapAddressModel
{
    private ArrayList<Result> results;

    public ArrayList<Result> getResults() { return this.results; }

    public void setResults(ArrayList<Result> results) { this.results = results; }

    private String status;

    public String getStatus() { return this.status; }

    public void setStatus(String status) { this.status = status; }
}
