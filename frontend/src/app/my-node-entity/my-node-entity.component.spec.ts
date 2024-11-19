import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyNodeEntityComponent } from './my-node-entity.component';

describe('MyNodeEntityComponent', () => {
  let component: MyNodeEntityComponent;
  let fixture: ComponentFixture<MyNodeEntityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyNodeEntityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyNodeEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
