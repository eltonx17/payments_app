import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TabInterfaceComponent } from './tab-interface/tab-interface.component';
import { ConfigComponent } from './config/config.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TabInterfaceComponent, ConfigComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'ng-payments';
}
