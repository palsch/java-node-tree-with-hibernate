export interface NodeEntity {
  id: string;
  dtype: string;
  childNodes: NodeEntity[];
  requiredDocumentUpload?: DocumentUpload;
  optionalDocumentUpload?: DocumentUpload;
}

export interface DocumentUpload {
  id?: string;
  aleAntragId?: string;
  answerId?: string;
  docTypes: Array<string>;
  attachments?: Array<string>;
  maxDocsCount: number;
  required?: boolean;
  /**
   * Use can set this flag to true if the upload should be done later after the form is submitted.
   * This flag is used to calculate the validation state of the form when a required document is not uploaded.
   */
  laterUpload?: boolean;
}

export interface OverviewItem {
  antragId: string;
  status: string;
  updatedAt: string;
  nodeCount: number;
}

export interface NodeAttrbute {
  name: string;
  value: any;
  type: string;
}
