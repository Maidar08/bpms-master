import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ErinTreeNode} from './erin-tree-node.component';

describe('ErinTreeComponent', () => {
  let component: ErinTreeNode;
  let fixture: ComponentFixture<ErinTreeNode>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErinTreeNode ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinTreeNode);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
