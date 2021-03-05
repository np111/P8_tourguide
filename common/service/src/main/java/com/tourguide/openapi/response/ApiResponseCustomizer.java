package com.tourguide.openapi.response;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
@Order(1)
public class ApiResponseCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        for (ApiNullResponse res : handlerMethod.getMethod().getAnnotationsByType(ApiNullResponse.class)) {
            addApiNullResponse(operation, handlerMethod, res);
        }
        return operation;
    }

    private void addApiNullResponse(Operation operation, HandlerMethod handlerMethod, ApiNullResponse res) {
        operation.getResponses().addApiResponse("204", new ApiResponse().description(res.description()));
    }
}
