package com.tourguide.model;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * An error returned by the API.
 * <p>
 * Read endpoint documentations for information about possibles {@code code} and {@code metadata}.
 */
@lombok.Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class ApiError {
    /**
     * The type of error, indicating who is responsible (if known) for a failed request.
     */
    private @lombok.Builder.Default ErrorType type = ErrorType.UNKNOWN;

    /**
     * The error code, allowing to identify its cause and to manage it correctly.
     */
    private String code;

    /**
     * A message to aiding developers to debug.
     */
    private String message;

    /**
     * Some metadata about the error allowing to identify its cause in depth.
     */
    @Singular("metadata")
    private Map<String, Object> metadata;

    /**
     * @see ApiError
     */
    public enum ErrorType {
        /**
         * The error came from the client (eg.: invalid authorization, missing parameters) and the request could not be processed.
         */
        CLIENT,

        /**
         * The error comes from the service (eg.: a requested resource is missing) and must be handled by the client. Possible errors are documented for each endpoint.
         */
        SERVICE,

        /**
         * The error is unexpected and of unknown source (eg.: network issue, internal service exception).
         */
        UNKNOWN,
    }
}
