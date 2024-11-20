import { Component, inject, input } from '@angular/core';
import { OverviewItem } from '../node.types';
import { MatCard, MatCardContent, MatCardHeader, MatCardSubtitle, MatCardTitle } from '@angular/material/card';
import { DatePipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { Clipboard } from '@angular/cdk/clipboard';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-my-overview-item',
  standalone: true,
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    DatePipe,
    MatIcon,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './my-overview-item.component.html',
  styleUrl: './my-overview-item.component.scss'
})
export class MyOverviewItemComponent {
  overviewItem = input.required<OverviewItem>();

  private _clipboard = inject(Clipboard);

  // highlight the overview item if the antrag id is the same as the one in the URL path - use ActiveRoute to highlight the item


  copyId(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._clipboard.copy(this.overviewItem().antragId);
  }

}
