import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyOverviewItemComponent } from './my-overview-item.component';

describe('MyOverviewItemComponent', () => {
  let component: MyOverviewItemComponent;
  let fixture: ComponentFixture<MyOverviewItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyOverviewItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyOverviewItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
