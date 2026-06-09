package com.tcc_vizinhanca.vizinhanca.controller.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockListSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategorySummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.activity_view.ActivityViewDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.activity_view.ActivityViewResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.object.ObjectDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.object.ObjectSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.report.ReportDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.report.ReportSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.mapper.condominium.CondominiumMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.block.BlockService;
import com.tcc_vizinhanca.vizinhanca.service.category.CategoryService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.object.ObjectService;
import com.tcc_vizinhanca.vizinhanca.service.report.ReportService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.service.service.ServiceService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/condominium")
@Tag(name = "Condominium", description = "Endpoints para gerenciamento dos condomínios.")
public class CondominiumController {

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ActivityViewService activityViewService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ObjectService objectService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private CategoryService categoryService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<CondominiumResponse>> listAllCondos() {
        List<Condominium> condominiums = condominiumService.getSelectAllCondominiums();
        CondominiumResponse response = new CondominiumResponse(condominiums);
        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de condomínios retornada com sucesso!"));
    }

    // GET RESIDENTS
    @GetMapping("/resident/me")
    public ResponseEntity<ApiResponse<PageResponse<ResidentSummaryResponse>>> listAllResidentsByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Resident> residents = residentService.getSelectResidentsByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ResidentSummaryResponse> response =
                new PageResponse<>(residents, ResidentSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Moradores encontrados com sucesso!"));
    }

    // GET RESIDENTS WITH FILTERS
    @GetMapping("/resident/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ResidentSummaryResponse>>> listResidentsByFilters(
            @RequestParam(required = false) List<Long> blockIds,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Resident> residents = residentService
                .getSelectResidentsByFilters(user.idCondominium(), blockIds, isActive, name, pageable);

        PageResponse<ResidentSummaryResponse> response =
                new PageResponse<>(residents, ResidentSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Moradores filtrados retornados com sucesso!"));
    }

    // GET ACTIVITIES
    @GetMapping("/activity/me")
    public ResponseEntity<ApiResponse<ActivityViewResponse>> listAllActivitiesByCondominium(
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ActivityView> activities = activityViewService
                .getSelectActivitiesViewByCondominiumId(user.idCondominium());

        ActivityViewResponse response = new ActivityViewResponse(activities);
        return ResponseEntity.ok(ResponseUtil.success(response, "Atividades encontradas com sucesso!"));
    }

    // GET ACTIVITIES WITH FILTERS
    @GetMapping("/activity/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ActivityViewDetailResponse>>> listActivitiesByFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long idBlock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataCriacao").descending());
        Page<ActivityView> activities = activityViewService
                .getSelectActivitiesByFilters(user.idCondominium(), status, type, idBlock, pageable);

        PageResponse<ActivityViewDetailResponse> response =
                new PageResponse<>(activities, ActivityViewDetailResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Atividades filtradas retornadas com sucesso!"));
    }

    // GET SERVICES
    @GetMapping("/service/me")
    public ResponseEntity<ApiResponse<PageResponse<ServiceSummaryResponse>>> listAllServicesByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int  size,
            HttpServletRequest request) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Service> services = serviceService.getSelectAllServicesByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ServiceSummaryResponse> response = new PageResponse<>(services, ServiceSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Serviços encontrados com sucesso!"));

    }

    // GET SERVICES WITH FILTERS
    @GetMapping("/service/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listServicesByFilters(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> blockIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService
                .getSelectServicesByFilters(user.idCondominium(), statuses, categoryIds, blockIds, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços filtrados retornados com sucesso!"));
    }

    // GET OBJECTS
    @GetMapping("/object/me")
    public ResponseEntity<ApiResponse<PageResponse<ObjectSummaryResponse>>> listAllServiceByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int  size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Object> objects = objectService.getSelectAllObjectsByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ObjectSummaryResponse> response = new PageResponse<>(objects, ObjectSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Objectos encontrados com sucesso!"));
    }

    // GET OBJECTS WITH FILTERS
    @GetMapping("/object/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listObjectsByFilters(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> blockIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService
                .getSelectObjectsByFilters(user.idCondominium(), statuses, categoryIds, blockIds, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos filtrados retornados com sucesso!"));
    }

    // GET REPORTS
    @GetMapping("/report/me")
    public ResponseEntity<ApiResponse<PageResponse<ReportSummaryResponse>>> listAllReportsByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int  size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("reasonReport").ascending());
        Page<Report> reports = reportService.getSelectReportsByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ReportSummaryResponse> response = new PageResponse<>(reports, ReportSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Denúncias encontradas com sucesso!"));
    }

    // GET REPORTS WITH FILTERS
    @GetMapping("/report/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ReportDetailResponse>>> listReportsByFilters(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<Long> reasonIds,
            @RequestParam(required = false) List<Long> blockIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Report> reports = reportService
                .getSelectReportsByFilters(user.idCondominium(), statuses, reasonIds, blockIds, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(reports, ReportDetailResponse::new),
                "Denúncias filtradas retornadas com sucesso!"));
    }

    // GET BLOCKS
    @GetMapping("/block/me")
    public ResponseEntity<ApiResponse<BlockListSummaryResponse>> listAllBlocksByCondominium(HttpServletRequest request) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Block> blocks = blockService.getSelectBlocksByCondominium(user.idCondominium());

        BlockListSummaryResponse response = new BlockListSummaryResponse(blocks);

        return ResponseEntity.ok(ResponseUtil.success(response, "Blocos encontrados com sucesso!"));

    }

    // GET CATEGORIES
    @GetMapping("/category/me")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> listAllCategoriesByCondominium(HttpServletRequest request) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Category> categories = categoryService.getSelectCategoriesByCondominiumId(user.idCondominium());

        List<CategorySummaryResponse> response = categories.stream()
                .map(CategorySummaryResponse::new)
                .toList();

        return ResponseEntity.ok(ResponseUtil.success(response, "Categorias encontradas com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> searchCondominiumById(
            @PathVariable("id") Long idCondominium) {

        Condominium condominium = condominiumService.getSelectCondominiumById(idCondominium);
        List<ActivityView> activities = activityViewService
                .getSelectActivitiesViewByCondominiumId(idCondominium);

        CondominiumDetailResponse response = new CondominiumDetailResponse(condominium, activities);
        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> insertCondominium(
            @Valid @RequestBody CondominiumCreateRequest condominiumCreateRequest) {

        Condominium condominium = CondominiumMapper.toEntity(condominiumCreateRequest);
        Condominium newCondominium = condominiumService.setInsertCondominium(condominium);
        CondominiumDetailResponse response = new CondominiumDetailResponse(newCondominium);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Condomínio criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> updateCondominium(
            @PathVariable Long id,
            @Valid @ModelAttribute CondominiumUpdateRequest condominiumUpdateRequest) {

        Condominium condominium = CondominiumMapper.updateEntity(condominiumUpdateRequest, new Condominium());
        Condominium updatedCondominium = condominiumService.setUpdateCondominium(
                condominium, condominiumUpdateRequest.getFoto(), id);

        CondominiumDetailResponse response = new CondominiumDetailResponse(updatedCondominium);
        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCondominium(@PathVariable Long id) {
        condominiumService.setDeleteCondominiumById(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Condomínio deletado com sucesso!"));
    }
}
