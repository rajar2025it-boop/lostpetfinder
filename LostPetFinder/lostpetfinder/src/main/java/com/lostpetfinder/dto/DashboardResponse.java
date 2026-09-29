package com.lostpetfinder.dto;

public record DashboardResponse(long totalUsers,
                                long totalLostReports, long activeLostReports, long resolvedLostReports,
                                long totalFoundReports, long activeFoundReports, long resolvedFoundReports) {}
