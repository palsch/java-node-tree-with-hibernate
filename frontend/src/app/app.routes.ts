import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { MyAntragComponent } from './my-antrag/my-antrag.component';

export const routes: Routes = [
  {path: 'antrag/:antragId', component: MyAntragComponent},
];
