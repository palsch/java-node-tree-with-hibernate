import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { NodeEntity, OverviewItem } from './node.types';

@Injectable({
  providedIn: 'root'
})
export class BackendServiceService {

  private aleAntragUrl = '/api/ale_antrag';

  public get_antrag_time = 0;
  public update_node_time = 0;

  constructor(public httpClient: HttpClient) {
  }

  public getOverview(): Observable<OverviewItem[]> {
    return this.httpClient.get<OverviewItem[]>(this.aleAntragUrl);
  }

  public createAleAntrag(): Observable<NodeEntity> {
    return this.httpClient.post<NodeEntity>(this.aleAntragUrl, {})
  }

  public getAleAntrag(id: string): Observable<NodeEntity> {
    const start = Date.now();
    return this.httpClient.get<NodeEntity>(`${this.aleAntragUrl}/${id}`)
      .pipe(
        tap(() => this.get_antrag_time = Date.now() - start)
      );
  }

  //### add a child
  // POST http://localhost:9999/api/ale_antrag/{{antrag_id}}/{{child_question_id}}/child-nodes
  public addNode(antragId: string, childQuestionId: string): Observable<NodeEntity> {
    return this.httpClient.post<NodeEntity>(`${this.aleAntragUrl}/${antragId}/${childQuestionId}/child-nodes`, {});
  }


  // ### remove a child
  // DELETE http://localhost:9999/api/ale_antrag/{{antrag_id}}/child-nodes/8e0a1fa0-ab9a-48f9-b60a-e1bda87e9b2a
  public removeNode(antragId: string, nodeId: string): Observable<NodeEntity> {
    return this.httpClient.delete<NodeEntity>(`${this.aleAntragUrl}/${antragId}/child-nodes/${nodeId}`);
  }


  //### update node
  //  PATCH http://localhost:9999/api/ale_antrag/{{antrag_id}}/child-nodes
  //    Content-Type: application/json
  public saveNode(antragId: string, node: NodeEntity): Observable<NodeEntity> {
    const start = Date.now();
    return this.httpClient.patch<NodeEntity>(`${this.aleAntragUrl}/${antragId}/child-nodes`, node)
      .pipe(
        tap(() => this.update_node_time = Date.now() - start)
      );
  }
}
