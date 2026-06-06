package com.mchpixel.analy.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

public class ConfigManager {

    // Add the logger to this class to use for certain things!
    private final java.util.logging.Logger logger;


    // The final Strings and Ints for the values
    private final String serverId;
    private final String apiKey;
    private final String backendUrl;
    private final Integer flushIntervalSeconds;
    private final Integer maxSize;
    private final Boolean verifyCertificate;


    // This pulls all the values from the plugins config.yml
    // and assigns them to their individual final Strings
    public ConfigManager(Logger logger, Plugin plugin) {
        this.logger = logger;


        this.serverId = plugin.getConfig()
                .getString("analy.server-id", "default-server");

        this.apiKey = plugin.getConfig()
                .getString("analy.api-key", "ABCD-EFGH-IJKL-MNOP-QRST-UVWX-YZ01-2345-6789");

        this.backendUrl = plugin.getConfig()
                .getString("analy.backend-url", "http://localhost:5000");

        this.flushIntervalSeconds = plugin.getConfig()
                .getInt("analy.buffer.flush-interval-seconds", 10);

        this.maxSize = plugin.getConfig()
                .getInt("analy.buffer.max-size", 500);

        this.verifyCertificate = plugin.getConfig()
                .getBoolean("analy.tls.verify-certificate", true);
    }


    // A getter for each value so it is final and cant be
    // touched and so it is all in one place!
    public String get_server_id() {
        return serverId;
    }

    public String get_api_key() {
        return apiKey;
    }

    public String get_backend_url() {
        return backendUrl;
    }

    public Integer get_flush_interval_seconds() {
        return flushIntervalSeconds;
    }

    public Integer get_max_buffer_size() {
        return maxSize;
    }

    public Boolean get_verify_certificate() {
        return verifyCertificate;
    }


    // Using regEx expressions we check if the content
    // is consistent with what we expect as values
    public boolean validate() {

        // Validate Server ID
        Pattern serverId = Pattern.compile("[^A-Za-z0-9_]");
        Matcher matchServerId = serverId.matcher(get_server_id());

        boolean serverIdFound = matchServerId.find();


        // Validate Api Key
        Pattern apiKey = Pattern.compile("^[A-Za-z0-9]{4}(?:-[A-Za-z0-9]{4}){8}$");
        Matcher matchApiKey = apiKey.matcher(get_api_key());

        boolean apiKeyFound = matchApiKey.find();


        // Validate Backend URL
        Pattern backendUrl = Pattern.compile("https?://[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+");
        Matcher matchBackendUrl = backendUrl.matcher(get_backend_url());

        boolean backendUrlFound = matchBackendUrl.find();


        // Validate Flush Interval
        Pattern flushInterval = Pattern.compile("[0-9]");
        Matcher matchFlushInterval = flushInterval.matcher(get_flush_interval_seconds().toString());

        boolean flushIntervalFound = matchFlushInterval.find();


        // Validate Buffer Size
        Pattern bufferSize = Pattern.compile("[0-9]");
        Matcher matchBufferSize = bufferSize.matcher(get_max_buffer_size().toString());

        boolean bufferSizeFound = matchBufferSize.find();


        // Validate Flush Interval
        Pattern verifyCertificate = Pattern.compile("[a-z]");
        Matcher matchVerifyCertificate = verifyCertificate.matcher(get_verify_certificate().toString());

        boolean verifyCertificateFound = matchVerifyCertificate.find();




        // Get Flush Interval and Buffer
        Integer valFlushInterval = get_flush_interval_seconds();
        Integer valBufferMax = get_max_buffer_size();


        // Validate each field individually with a clear error per failure
        if (!serverIdFound) {
            logger.severe("[Analy] server-id contains invalid characters! Only letters, numbers and underscores are allowed.");
            return false;
        }

        if (!apiKeyFound) {
            logger.severe("[Analy] api-key format is invalid! Expected format: ABCD-EFGH-IJKL-MNOP-QRST-UVWX-YZ01-2345-6789");
            return false;
        }

        if (!backendUrlFound) {
            logger.severe("[Analy] backend-url is invalid! Must start with http:// or https://");
            return false;
        }

        if (valFlushInterval < 5 || valFlushInterval > 300) {
            logger.severe("[Analy] flush-interval-seconds must be between 5 and 300!");
            return false;
        }

        if (valBufferMax < 100 || valBufferMax > 5000) {
            logger.severe("[Analy] max-size must be between 100 and 5000!");
            return false;
        }

        // Everything passed!
        return true;

    }

}
