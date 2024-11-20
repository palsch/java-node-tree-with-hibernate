import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { Observable, of } from 'rxjs';
import { NodeEntity, OverviewItem } from './node.types';
import { AsyncPipe } from '@angular/common';
import { BackendServiceService } from './backend-service.service';
import { MyOverviewItemComponent } from './my-overview-item/my-overview-item.component';
import { MatAnchor } from '@angular/material/button';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatGridList, MatGridTile, MatGridTileText } from '@angular/material/grid-list';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AsyncPipe, MyOverviewItemComponent, MatAnchor, RouterLink, RouterOutlet, MatChip, MatChipSet, MatCardContent, MatCard, MatGridTile, MatGridList, MatGridTileText],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  overview$: Observable<OverviewItem[]> = of([]);

  constructor(public backendService: BackendServiceService) {
  }

  ngOnInit(): void {
    this.overview$ = this.backendService.getOverview();
  }

  createAntrag(): void {
    // create a new antrag and navigate to it
    this.backendService.createAleAntrag().subscribe(
      (antrag: NodeEntity) => {
        window.location.href = '/antrag/' + antrag.id;
      }
    );
  }

}
