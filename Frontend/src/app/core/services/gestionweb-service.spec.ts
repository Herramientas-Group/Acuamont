import { TestBed } from '@angular/core/testing';

import { GestionwebService } from './gestionweb-service';

describe('GestionwebService', () => {
  let service: GestionwebService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GestionwebService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
