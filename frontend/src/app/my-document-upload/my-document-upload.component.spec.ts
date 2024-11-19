import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyDocumentUploadComponent } from './my-document-upload.component';

describe('MyDocumentUploadComponent', () => {
  let component: MyDocumentUploadComponent;
  let fixture: ComponentFixture<MyDocumentUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyDocumentUploadComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyDocumentUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
