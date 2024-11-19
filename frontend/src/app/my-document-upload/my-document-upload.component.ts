import { Component, input } from '@angular/core';
import { DocumentUpload } from '../node.types';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-my-document-upload',
  standalone: true,
  imports: [
    JsonPipe
  ],
  templateUrl: './my-document-upload.component.html',
  styleUrl: './my-document-upload.component.scss'
})
export class MyDocumentUploadComponent {
  docUpload = input.required<DocumentUpload | undefined>();
}
