package com.bakuard.flashcards.controller;

import com.bakuard.flashcards.config.security.RequestContext;
import com.bakuard.flashcards.dto.DtoMapper;
import com.bakuard.flashcards.dto.exceptions.ExceptionResponse;
import com.bakuard.flashcards.dto.expression.ExpressionForRepetitionResponse;
import com.bakuard.flashcards.dto.expression.ExpressionRepeatRequest;
import com.bakuard.flashcards.dto.expression.ExpressionResponse;
import com.bakuard.flashcards.model.expression.Expression;
import com.bakuard.flashcards.service.ExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Повторение устойчевых выражений пользователя")
@RestController
@RequestMapping("/repetition/words")
public class RepetitionOfExpressionsController {

    private static final Logger logger = LoggerFactory.getLogger(RepetitionOfExpressionsController.class.getName());


    private ExpressionService expressionService;
    private DtoMapper mapper;
    private RequestContext requestContext;

    @Autowired
    public RepetitionOfExpressionsController(ExpressionService expressionService,
                                             DtoMapper mapper,
                                             RequestContext requestContext) {
        this.expressionService = expressionService;
        this.mapper = mapper;
        this.requestContext = requestContext;
    }

    @Operation(summary = "Возвращает часть выборки устойчевых выражений доступных для повторения в текущую дату",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            description = "Если нарушен хотя бы один из инвариантов связаный с параметрами запроса",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "401",
                            description = "Если передан некорректный токен или токен не указан",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Page<ExpressionForRepetitionResponse>> findAllBy(
            @RequestParam("page")
            @Parameter(description = "Номер страницы выборки. Нумерация начинается с нуля.", required = true)
            int page,
            @RequestParam(value = "size", required = false)
            @Parameter(description = "Размер страницы выборки. Диапозон значений - [1, 100].")
            int size) {
        UUID userId = requestContext.getCurrentJwsBody(UUID.class);
        logger.info("user {} find all expressions for repeat by page={}, size={}", userId, page, size);

        Pageable pageable = mapper.toPageableForDictionaryExpressions(page, size, "value.asc");
        Page<Expression> result = expressionService.findAllForRepeat(userId, pageable);

        return ResponseEntity.ok(mapper.toExpressionForRepetitionResponse(result));
    }

    @Operation(summary = "Отмечает - помнит ли пользователь устойчевое выражение или нет.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            description = "Если нарушен хотя бы один из инвариантов связаный с телом запроса",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "401",
                            description = "Если передан некорректный токен или токен не указан",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    @PutMapping
    public ResponseEntity<ExpressionResponse> repeat(@RequestBody ExpressionRepeatRequest dto) {
        UUID userId = requestContext.getCurrentJwsBody(UUID.class);
        logger.info("user {} repeat expression {}. remember is {}", userId, dto.getExpressionId(), dto.isRemember());

        Expression expression = expressionService.repeat(userId, dto.getExpressionId(), dto.isRemember());

        return ResponseEntity.ok(mapper.toExpressionResponse(expression));
    }

}
