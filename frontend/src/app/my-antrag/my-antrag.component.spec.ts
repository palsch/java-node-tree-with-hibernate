import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyAntragComponent } from './my-antrag.component';

describe('MyAntragComponent', () => {
  let component: MyAntragComponent;
  let fixture: ComponentFixture<MyAntragComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyAntragComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyAntragComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
