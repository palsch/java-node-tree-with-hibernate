import { Component, computed, effect, input, output, signal } from '@angular/core';
import { NodeAttributesPipe } from '../node-attributes.pipe';
import { FormsModule } from '@angular/forms';
import { NodeEntity } from '../node.types';
import { MatAnchor } from '@angular/material/button';
import { JsonPipe } from '@angular/common';
import { MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { MatFormField, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput, MatInputModule } from '@angular/material/input';
import { MyDocumentUploadComponent } from '../my-document-upload/my-document-upload.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { BackendServiceService } from '../backend-service.service';
import { tap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { MatDatepicker, MatDatepickerInput, MatDatepickerModule, MatDatepickerToggle } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';

@Component({
  selector: 'app-my-node-entity',
  standalone: true,
  providers: [
    provideNativeDateAdapter(),
  ],
  imports: [
    FormsModule,
    MatAnchor,
    JsonPipe,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardActions,
    MatCardSubtitle,
    MatCardTitle,
    MatFormField,
    MatLabel,
    MatInput,
    MyDocumentUploadComponent,
    MatCheckbox,
    RouterLink,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    MatHint,
    MatFormFieldModule, MatInputModule, MatDatepickerModule
  ],
  templateUrl: './my-node-entity.component.html',
  styleUrl: './my-node-entity.component.scss'
})
export class MyNodeEntityComponent {
  antragId = input.required<string>();
  nodeEntity = input.required<NodeEntity>();
  level = input.required<number>();

  removeNode = output<NodeEntity | null>();

  readonly attrbutesPipe = new NodeAttributesPipe();

  attributes = computed(() => this.attrbutesPipe.transform(this.nodeEntityToRender(), ['requiredDocumentUpload', 'optionalDocumentUpload']));

  nodeEntityToRender = signal<NodeEntity | null>(null);

  private modelChangeTimeout: any;

  constructor(private backendService: BackendServiceService) {
    effect(() => {
      this.nodeEntityToRender.set(this.nodeEntity());
    }, { allowSignalWrites: true });
  }

  modelChange(): void {
    if (this.modelChangeTimeout) {
      clearTimeout(this.modelChangeTimeout);
    }

    this.modelChangeTimeout = setTimeout(() => {
      this.saveNode();
    }, 2000);
  }

  saveNode(): void {
    // make a copy of the node entity (ignore childNodes)
    const nodeEntityCopy = { ...this.nodeEntityToRender(), childNodes: [] } as any;

    // save all attribute changes to node entity and sent that to backend
    this.attributes().forEach(a => nodeEntityCopy[a.name] = a.value);

    console.log('Save node', nodeEntityCopy);
    this.backendService.saveNode(this.antragId(), nodeEntityCopy)
      .pipe(
        tap(a => console.log('saved node', a)),
        tap(a => this.nodeEntityToRender.set(a))
      )
      .subscribe();
  }

  removeNodeClick(): void {
    console.log('Remove node', this.nodeEntityToRender);
    this.removeNode.emit(this.nodeEntityToRender());
  }

  onRemoveNode(node: NodeEntity | null): void {
    if (!node) {
      return;
    }
    
    this.backendService.removeNode(this.antragId(), node.id)
      .pipe(
        tap(a => console.log('removed node', node)),
        tap(a => this.nodeEntity().childNodes = this.nodeEntity().childNodes.filter(n => n.id !== node.id))
      )
      .subscribe();
  }

  addNode(): void {
    console.log('Add node', this.nodeEntity);
    this.backendService.addNode(this.antragId(), this.nodeEntity().id)
      .pipe(
        tap(a => console.log('added node', a)),
        tap(a => this.nodeEntity().childNodes.push(a))
      )
      .subscribe();
  }
}
