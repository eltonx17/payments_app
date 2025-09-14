import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './config.component.html',
  styleUrl: './config.component.css'
})
export class ConfigComponent implements OnInit {
  apiHost = 'http://localhost:8080';
  message = '';

  constructor(private apiService: ApiService) {}

  saveConfig() {
    if (!this.apiHost) {
      this.message = 'Please enter an API host URL';
      return;
    }

    // Remove trailing slash if present
    this.apiHost = this.apiHost.replace(/\/$/, '');
    
    this.apiService.setBaseUrl(this.apiHost);
    this.message = 'API host configured successfully!';
    
    // Store in localStorage for persistence
    localStorage.setItem('apiHost', this.apiHost);
  }

  ngOnInit() {
    // Load from localStorage if available, otherwise use default
    const savedHost = localStorage.getItem('apiHost');
    if (savedHost) {
      this.apiHost = savedHost;
      this.apiService.setBaseUrl(savedHost);
    } else {
      // Set default API host
      this.apiService.setBaseUrl(this.apiHost);
    }
  }
}
