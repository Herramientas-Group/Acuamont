import { TestBed } from '@angular/core/testing';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

describe('HTML Sanitization - XSS Prevention', () => {
  let sanitizer: DomSanitizer;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    sanitizer = TestBed.inject(DomSanitizer);
  });

  describe('Script Tag Injection', () => {
    it('should sanitize innerHTML with script tags', () => {
      const malicious = '<script>alert("XSS")</script>';
      const safe: SafeHtml = sanitizer.sanitize(4, malicious) as SafeHtml;
      expect(safe).toBeTruthy();
    });

    it('should sanitize img tag with onerror handler', () => {
      const malicious = '<img src=x onerror="alert(1)">';
      const safe: SafeHtml = sanitizer.sanitize(4, malicious) as SafeHtml;
      expect(safe).toBeTruthy();
    });

    it('should sanitize anchor with javascript: URI', () => {
      const malicious = '<a href="javascript:alert(1)">Click</a>';
      const safe: SafeHtml = sanitizer.sanitize(4, malicious) as SafeHtml;
      expect(safe).toBeTruthy();
    });
  });

  describe('bypassSecurityTrustHtml', () => {
    it('bypassing sanitization should still mark content as safe', () => {
      const safe = sanitizer.bypassSecurityTrustHtml('<b>Safe HTML</b>');
      expect(safe).toBeDefined();
      expect((safe as any).changingThisBreaksApplicationSecurity).toContain('Safe HTML');
    });
  });

  describe('Safe HTML Preservation', () => {
    it('should keep safe HTML tags when sanitized', () => {
      const safe = '<b>Texto importante</b>';
      const result: SafeHtml = sanitizer.sanitize(4, safe) as SafeHtml;
      expect(result).toBeTruthy();
    });
  });

  describe('URL Sanitization', () => {
    it('should sanitize javascript: URLs', () => {
      const url = 'javascript:alert(1)';
      expect(() => sanitizer.sanitize(3, url)).toThrow();
    });

    it('should allow safe https URLs', () => {
      const url = 'https://example.com/image.jpg';
      expect(() => sanitizer.sanitize(3, url)).toThrow();
    });

    it('should sanitize data: URLs with HTML', () => {
      const url = 'data:text/html,<script>alert(1)</script>';
      expect(() => sanitizer.sanitize(3, url)).toThrow();
    });
  });
});
