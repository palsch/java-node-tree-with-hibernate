import { Component, input, output } from '@angular/core';
import { OverviewItem } from '../node.types';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-overview-item',
  standalone: true,
  imports: [
    MatAnchor,
    RouterLink
  ],
  templateUrl: './my-overview-item.component.html',
  styleUrl: './my-overview-item.component.scss'
})
export class MyOverviewItemComponent {
  overviewItem = input.required<OverviewItem>();

  openAntrag = output<OverviewItem>();
}
