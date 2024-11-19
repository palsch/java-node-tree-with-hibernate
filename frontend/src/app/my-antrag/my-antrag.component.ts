import { Component, effect, input } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { MyNodeEntityComponent } from '../my-node-entity/my-node-entity.component';
import { Observable, of } from 'rxjs';
import { NodeEntity } from '../node.types';
import { ActivatedRoute } from '@angular/router';
import { BackendServiceService } from '../backend-service.service';

@Component({
  selector: 'app-my-antrag',
  standalone: true,
  imports: [
    AsyncPipe,
    MyNodeEntityComponent
  ],
  templateUrl: './my-antrag.component.html',
  styleUrl: './my-antrag.component.scss'
})
export class MyAntragComponent {

  antragId = input<string>();

  // @ts-ignore
  antrag$: Observable<NodeEntity>;


  constructor(private route: ActivatedRoute, private backendService: BackendServiceService) {
    effect(() => {
      console.log('antragId', this.antragId());
      this.openAntrag(this.antragId());
    });
  }
  openAntrag(antragId: string | undefined): void {
    if (!antragId) {
      return;
    }
    console.log('open antrag', antragId);
    this.backendService.getAleAntrag(antragId).subscribe(a => this.antrag$ = of(a));
  }

}
