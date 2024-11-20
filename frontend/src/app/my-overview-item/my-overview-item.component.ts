import { Component, inject, input, output } from '@angular/core';
import { OverviewItem } from '../node.types';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { DatePipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-my-overview-item',
  standalone: true,
  imports: [
    MatAnchor,
    RouterLink,
    MatCard,
    MatCardContent,
    MatCardActions,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    DatePipe,
    MatIcon
  ],
  templateUrl: './my-overview-item.component.html',
  styleUrl: './my-overview-item.component.scss'
})
export class MyOverviewItemComponent {
  overviewItem = input.required<OverviewItem>();


  private _clipboard = inject(Clipboard);
  copyId(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._clipboard.copy(this.overviewItem().antragId);
  }

}
