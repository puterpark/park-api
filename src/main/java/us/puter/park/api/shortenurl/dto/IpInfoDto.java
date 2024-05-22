package us.puter.park.api.shortenurl.dto;

public record IpInfoDto(
        String status,
        String country,
        String countryCode,
        String region,
        String regionName,
        String city,
        String zip,
        Float lat,
        Float lon,
        String isp,
        String org
) {
}
