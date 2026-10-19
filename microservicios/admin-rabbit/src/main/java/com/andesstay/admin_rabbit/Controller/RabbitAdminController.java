package com.andesstay.admin_rabbit.Controller;

import com.andesstay.admin_rabbit.DTO.BindingInfoDTO;
import com.andesstay.admin_rabbit.DTO.BindingRequest;
import com.andesstay.admin_rabbit.DTO.ExchangeCreateRequest;
import com.andesstay.admin_rabbit.DTO.ExchangeInfoDTO;
import com.andesstay.admin_rabbit.DTO.NamePatterns;
import com.andesstay.admin_rabbit.DTO.OperationResultDTO;
import com.andesstay.admin_rabbit.DTO.QueueCreateRequest;
import com.andesstay.admin_rabbit.DTO.QueueInfoDTO;
import com.andesstay.admin_rabbit.Service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de administración de RabbitMQ (solo rol Admin, ver SecurityConfig).
 *
 *  GET    /api/admin/rabbit/queues                      listar colas
 *  GET    /api/admin/rabbit/queues/{name}               detalle de una cola
 *  POST   /api/admin/rabbit/queues                      crear cola
 *  DELETE /api/admin/rabbit/queues/{name}               eliminar cola
 *  DELETE /api/admin/rabbit/queues/{name}/messages      vaciar cola (purge)
 *
 *  GET    /api/admin/rabbit/exchanges                   listar exchanges
 *  POST   /api/admin/rabbit/exchanges                   crear exchange
 *  DELETE /api/admin/rabbit/exchanges/{name}            eliminar exchange
 *
 *  GET    /api/admin/rabbit/bindings                    listar bindings
 *  POST   /api/admin/rabbit/bindings                    crear binding
 *  DELETE /api/admin/rabbit/bindings?exchange=&queue=&routingKey=   eliminar binding
 */
@RestController
@RequestMapping("/api/admin/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) {
        this.service = service;
    }

    // ---------- Colas ----------

    @GetMapping("/queues")
    public ResponseEntity<List<QueueInfoDTO>> listQueues() {
        return ResponseEntity.ok(service.listQueues());
    }

    @GetMapping("/queues/{name}")
    public ResponseEntity<QueueInfoDTO> getQueue(@PathVariable String name) {
        return ResponseEntity.ok(service.getQueue(name));
    }

    @PostMapping("/queues")
    public ResponseEntity<OperationResultDTO> createQueue(@Valid @RequestBody QueueCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQueue(request));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> deleteQueue(
            @PathVariable @Pattern(regexp = NamePatterns.RESOURCE_NAME, message = NamePatterns.RESOURCE_NAME_MESSAGE) String name) {
        service.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/queues/{name}/messages")
    public ResponseEntity<Void> purgeQueue(
            @PathVariable @Pattern(regexp = NamePatterns.RESOURCE_NAME, message = NamePatterns.RESOURCE_NAME_MESSAGE) String name) {
        service.purgeQueue(name);
        return ResponseEntity.noContent().build();
    }

    // ---------- Exchanges ----------

    @GetMapping("/exchanges")
    public ResponseEntity<List<ExchangeInfoDTO>> listExchanges() {
        return ResponseEntity.ok(service.listExchanges());
    }

    @PostMapping("/exchanges")
    public ResponseEntity<OperationResultDTO> createExchange(@Valid @RequestBody ExchangeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createExchange(request));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> deleteExchange(
            @PathVariable @Pattern(regexp = NamePatterns.RESOURCE_NAME, message = NamePatterns.RESOURCE_NAME_MESSAGE) String name) {
        service.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    // ---------- Bindings ----------

    @GetMapping("/bindings")
    public ResponseEntity<List<BindingInfoDTO>> listBindings() {
        return ResponseEntity.ok(service.listBindings());
    }

    @PostMapping("/bindings")
    public ResponseEntity<OperationResultDTO> createBinding(@Valid @RequestBody BindingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBinding(request));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> deleteBinding(
            @RequestParam @NotBlank(message = "exchange es obligatorio") String exchange,
            @RequestParam @NotBlank(message = "queue es obligatorio") String queue,
            @RequestParam(defaultValue = "") String routingKey) {
        service.deleteBinding(exchange, queue, routingKey);
        return ResponseEntity.noContent().build();
    }
}
