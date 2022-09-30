package com.bakuard.flashcards.controller;

import com.bakuard.flashcards.config.security.RequestContext;
import com.bakuard.flashcards.controller.message.Messages;
import com.bakuard.flashcards.dto.DtoMapper;
import com.bakuard.flashcards.dto.exceptions.ExceptionResponse;
import com.bakuard.flashcards.dto.expression.ExpressionAddRequest;
import com.bakuard.flashcards.dto.expression.ExpressionForDictionaryListResponse;
import com.bakuard.flashcards.dto.expression.ExpressionResponse;
import com.bakuard.flashcards.dto.expression.ExpressionUpdateRequest;
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

@Tag(name = "Словарь устойчевых выражений пользователя")
@RestController
@RequestMapping("/dictionary/expressions")
public class DictionaryOfExpressionsController {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryOfExpressionsController.class.getName());


    private ExpressionService expressionService;
    private DtoMapper mapper;
    private RequestContext requestContext;
    private Messages messages;

    @Autowired
    public DictionaryOfExpressionsController(ExpressionService expressionService,
                                             DtoMapper mapper,
                                             RequestContext requestContext,
                                             Messages messages) {
        this.expressionService = expressionService;
        this.mapper = mapper;
        this.requestContext = requestContext;
        this.messages = messages;
    }

    @Operation(summary = "Добавляет новое устойчевое выражение в словарь пользователя",
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
            })
    @PostMapping
    public ResponseEntity<ExpressionResponse> add(@RequestBody ExpressionAddRequest dto) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} add word '{}'", userId, dto.getValue());

        Expression expression = mapper.toExpression(dto, userId);
        expression = expressionService.save(expression);
        return ResponseEntity.ok(mapper.toExpressionResponse(expression));
    }

    @Operation(summary = "Обновляет устойчевое выражение в словаре пользователя",
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
    public ResponseEntity<ExpressionResponse> update(@RequestBody ExpressionUpdateRequest dto) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} update word {}", userId, dto.getExpressionId());

        Expression expression = mapper.toExpression(dto, userId);
        expression = expressionService.save(expression);
        return ResponseEntity.ok(mapper.toExpressionResponse(expression));
    }

    @Operation(summary = "Возвращает часть выборки устойчевых выражений из словаря пользователя",
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
    public ResponseEntity<Page<ExpressionForDictionaryListResponse>> findAllBy(
            @RequestParam
            @Parameter(description = "Номер страницы выборки. Нумерация начинается с нуля.", required = true)
            int page,
            @RequestBody(required = false)
            @Parameter(description = "Размер страницы выборки. Диапозон значений - [1, 100].")
            int size,
            @RequestParam(required = false)
            @Parameter(description = "Порядок сортировки.",
                    schema = @Schema(
                            defaultValue = "value.asc (Сортировка по значению в порядке возрастания).",
                            allowableValues = {
                                    "value - сортировка по значению",
                                    "repeat_interval - сортировка по интервалу повторения",
                                    "last_date_of_repeat - сортировка по дате поседнего повторения"
                            }
                    ))
            String sort) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} get expressions by page={}, size={}, sort={}", page, size, sort);

        Pageable pageable = mapper.toPageableForDictionaryExpressions(page, size, sort);
        Page<ExpressionForDictionaryListResponse> result = mapper.toExpressionForDictionaryListResponse(
                expressionService.findByUserId(userId, pageable)
        );

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Возвращает устойчевое выражение из словаря пользователя по его идентификатору",
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
    @GetMapping("/{id}")
    public ResponseEntity<ExpressionResponse> findById(
            @PathVariable
            @Parameter(description = "Уникальный идентификатор устойчевого выражения в формате UUID. Не может быть null.", required = true)
            UUID id) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} get expression by id={}", userId, id);

        Expression expression = expressionService.tryFindById(userId, id);
        return ResponseEntity.ok(mapper.toExpressionResponse(expression));
    }

    @Operation(summary = "Возвращает устойчевое выражение из словаря пользователя по его значению",
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
    @GetMapping("/value/{value}")
    public ResponseEntity<ExpressionResponse> findByValue(
            @PathVariable
            @Parameter(description = "Значение устойчевого выражения. Не может быть null.", required = true)
            String value) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} get expression by value '{}'", userId, value);

        Expression expression = expressionService.tryFindByValue(userId, value);
        return ResponseEntity.ok(mapper.toExpressionResponse(expression));
    }

    @Operation(summary = "Удаляет устойчевое выражение из словаря пользователя пользователя",
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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable
            @Parameter(description = "Уникальный идентификатор устойчевого выражения в формате UUID. Не может быть null.", required = true)
            UUID id) {
        UUID userId = requestContext.getCurrentJwsBody();
        logger.info("user {} delete expression by id={}", userId, id);

        expressionService.tryDeleteById(userId, id);
        return ResponseEntity.ok(messages.getMessage("dictionary.expressions.delete"));
    }

}