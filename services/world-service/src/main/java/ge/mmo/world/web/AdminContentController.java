package ge.mmo.world.web;

import ge.mmo.world.narrative.ContentImportService;
import ge.mmo.world.narrative.ContentImportService.ImportSummary;
import ge.mmo.world.narrative.ContentPackage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin content authoring. Gated to ROLE_ADMIN by SecurityConfig. This is the "stories are data"
 * pipeline: POST a Saga/Tale/Beat document and it becomes playable content with no SQL or code.
 */
@RestController
@RequestMapping("/api/admin/content")
public class AdminContentController {

    private final ContentImportService importer;

    public AdminContentController(ContentImportService importer) {
        this.importer = importer;
    }

    @PostMapping("/import")
    public ResponseEntity<ImportSummary> importContent(@RequestBody ContentPackage pkg) {
        return ResponseEntity.status(HttpStatus.CREATED).body(importer.importContent(pkg));
    }
}
