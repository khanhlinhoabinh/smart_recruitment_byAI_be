
package com.fourctc.tuyendungthongminh_be.service;

import com.fourctc.tuyendungthongminh_be.dto.CompanyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CompanyService {

    /* ===== Public ===== */
    List<CompanyDTO> getActiveCompanies();
    CompanyDTO getCompanyById(UUID id);

    /* ===== Admin/HR view ===== */
    List<CompanyDTO> getAllCompanies();
    CompanyDTO getCompanyByIdAdmin(UUID id);

    /* ===== CRUD ===== */
    CompanyDTO createCompany(CompanyDTO dto, String createdByEmail); // Principal.getName()
    CompanyDTO updateCompany(UUID id, CompanyDTO dto, String username);
    void deleteCompany(UUID id);

    /* ===== Verify/Featured (Admin) ===== */
    CompanyDTO approveCompany(UUID id);
    CompanyDTO rejectCompany(UUID id);
    CompanyDTO setFeatured(UUID id, boolean featured);

    /* ===== NEW: HR upload GPKD & request verify ===== */
    Map<String, Object> uploadBusinessRegistrationForCompany(UUID companyId, MultipartFile file, Principal principal);
    CompanyDTO requestVerificationForCompany(UUID companyId, Principal principal);
}
