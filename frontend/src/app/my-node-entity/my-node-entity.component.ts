import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { NodeAttributesPipe } from '../node-attributes.pipe';
import { FormsModule } from '@angular/forms';
import { NodeEntity } from '../node.types';
import { MatAnchor } from '@angular/material/button';
import { JsonPipe, NgClass } from '@angular/common';
import { MatFormField, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput, MatInputModule } from '@angular/material/input';
import { MyDocumentUploadComponent } from '../my-document-upload/my-document-upload.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { BackendServiceService } from '../backend-service.service';
import { catchError, filter, tap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { MatDatepicker, MatDatepickerInput, MatDatepickerModule, MatDatepickerToggle } from '@angular/material/datepicker';
import { MatOption } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
// Depending on whether rollup is used, moment needs to be imported differently.
// Since Moment.js doesn't have a default export, we normally need to import using the `* as`
// syntax. However, rollup creates a synthetic default module and we thus need to import it using
// the `default as` syntax.
// tslint:disable-next-line:no-duplicate-imports
import * as _moment from 'moment';
import { default as _rollupMoment } from 'moment';
import { MatSelect } from '@angular/material/select';
import { Clipboard } from '@angular/cdk/clipboard';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionPanel, MatExpansionPanelContent, MatExpansionPanelDescription, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';

const moment = _rollupMoment || _moment;

@Component({
  selector: 'app-my-node-entity',
  standalone: true,
  imports: [
    FormsModule,
    MatAnchor,
    JsonPipe,
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
    MatFormFieldModule, MatInputModule, MatDatepickerModule, MatOption, MatSelect, NgClass, MatIconModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanelDescription, MatExpansionPanelContent
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

  attributes = computed(() => this.attrbutesPipe.transform(this.nodeEntityToRender(), ['parentId', 'documentUploads', 'requiredDocumentUpload', 'optionalDocumentUpload']));

  nodeEntityToRender = signal<NodeEntity | null>(null);

  isOpen = input<boolean>(false);

  private modelChangeTimeout: any;
  private _snackBar = inject(MatSnackBar);

  readonly paymentType = ['MONTHLY', 'DAILY'];


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
    }, 1000);
  }

  private _clipboard = inject(Clipboard);

  copyId(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._clipboard.copy(this.nodeEntity().id);
  }

  saveNode(): void {
    if (this.modelChangeTimeout) {
      clearTimeout(this.modelChangeTimeout);
    }

    // make a copy of the node entity (ignore childNodes)
    const nodeEntityCopy = { ...this.nodeEntityToRender(), childNodes: [] } as any;

    // save all attribute changes to node entity and sent that to backend
    this.attributes()
      .filter(a => a.value !== '')
      .map(a => ({ ...a, value: moment.isMoment(a.value) ? a.value.format('YYYY-MM-DD') : a.value }))
      .forEach(a => nodeEntityCopy[a.name] = a.value);

    console.log('Save node', nodeEntityCopy);
    this.backendService.saveNode(this.antragId(), nodeEntityCopy)
      .pipe(
        tap(a => console.log('saved node', a)),
        tap(a => this.nodeEntityToRender.set(a)),
        tap(a => this._snackBar.open('Updated node: ' + a.dtype)),
        catchError(e => {
          this._snackBar.open('Error updating node: ' + e.message);
          return e;
        })
      )
      .subscribe();
  }

  removeNodeClick(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    console.log('Remove node', this.nodeEntityToRender());
    this.removeNode.emit(this.nodeEntityToRender());
  }

  onRemoveNode(node: NodeEntity | null): void {
    if (!node) {
      console.error('Remove node error: node is undefined');
      return;
    }

    this.backendService.removeNode(this.antragId(), node.id)
      .pipe(
        // don't do anything if node is not defined
        filter(() => !!this.nodeEntityToRender()),
        tap(a => console.log('removed node', node)),
        tap(a => this.nodeEntityToRender()!.childNodes = this.nodeEntityToRender()!.childNodes.filter(n => n.id !== node.id))
      )
      .subscribe();
  }

  addNode(): void {
    if (!this.nodeEntityToRender()) {
      console.error('Add node error: nodeEntityToRender is undefined');
      return;
    }
    console.log('Add node', this.nodeEntityToRender());

    // @ts-ignore
    this.backendService.addNode(this.antragId(), this.nodeEntityToRender().id)
      .pipe(
        // don't do anything if node is not defined
        filter(() => !!this.nodeEntityToRender()),
        tap(a => this.nodeEntityToRender()!.childNodes ? this.nodeEntityToRender()!.childNodes.push(a) : this.nodeEntityToRender()!.childNodes = [a]),
        tap(a => console.log('added node', a)),
      )
      .subscribe();
  }
}
