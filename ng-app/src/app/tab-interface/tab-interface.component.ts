import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-tab-interface',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './tab-interface.component.html',
  styleUrl: './tab-interface.component.css'
})
export class TabInterfaceComponent implements OnInit {
  activeTab: 'payments' | 'webhooks' = 'payments';
  
  // Payment form data
  paymentData = {
    firstName: '',
    lastName: '',
    zipCode: '',
    cardNumber: '',
    requestId: ''
  };
  
  // Webhook form data
  webhookData = {
    url: ''
  };
  
  // Loading states
  isPaymentLoading = false;
  isWebhookLoading = false;
  
  // Success/Error messages
  paymentMessage = '';
  webhookMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    // Load API host from localStorage if available
    const savedHost = localStorage.getItem('apiHost');
    if (savedHost) {
      this.apiService.setBaseUrl(savedHost);
    }
  }

  setActiveTab(tab: 'payments' | 'webhooks') {
    this.activeTab = tab;
    this.clearMessages();
  }

  clearMessages() {
    this.paymentMessage = '';
    this.webhookMessage = '';
  }

  generateRequestId(): string {
    const timestamp = Date.now().toString(36);
    const randomStr = Math.random().toString(36).substring(2, 15);
    return `req_${timestamp}_${randomStr}`;
  }

  createPayment() {
    if (!this.paymentData.firstName || !this.paymentData.lastName || 
        !this.paymentData.zipCode || !this.paymentData.cardNumber) {
      this.paymentMessage = 'Please fill in all payment fields';
      return;
    }

    // Generate a unique requestId
    this.paymentData.requestId = this.generateRequestId();

    this.isPaymentLoading = true;
    this.paymentMessage = '';

    this.apiService.createPayment(this.paymentData).subscribe({
      next: (result) => {
        this.paymentMessage = `Payment created successfully! Request ID: ${this.paymentData.requestId}`;
        this.isPaymentLoading = false;
      },
      error: (error) => {
        // Extract status from HTTP response body if available
        let statusMessage = 'Please try again.';
        if (error.error && error.error.status) {
          statusMessage = error.error.status;
        } else if (error.status) {
          statusMessage = `HTTP ${error.status}`;
        } else if (error.message) {
          statusMessage = error.message;
        }
        
        this.paymentMessage = `Error creating payment: ${statusMessage}`;
        console.error('Payment creation error:', error);
        this.isPaymentLoading = false;
      }
    });
  }

  createWebhook() {
    if (!this.webhookData.url) {
      this.webhookMessage = 'Please enter a webhook URL';
      return;
    }

    this.isWebhookLoading = true;
    this.webhookMessage = '';

    this.apiService.createWebhook(this.webhookData).subscribe({
      next: (result) => {
        this.webhookMessage = 'Webhook created successfully!';
        this.isWebhookLoading = false;
      },
      error: (error) => {
        // Extract status from HTTP response body if available
        let statusMessage = 'Please try again.';
        if (error.error && error.error.status) {
          statusMessage = error.error.status;
        } else if (error.status) {
          statusMessage = `HTTP ${error.status}`;
        } else if (error.message) {
          statusMessage = error.message;
        }
        
        this.webhookMessage = `Error creating webhook: ${statusMessage}`;
        console.error('Webhook creation error:', error);
        this.isWebhookLoading = false;
      }
    });
  }

  resetPaymentForm() {
    this.paymentData = {
      firstName: '',
      lastName: '',
      zipCode: '',
      cardNumber: '',
      requestId: ''
    };
    this.paymentMessage = '';
  }

  resetWebhookForm() {
    this.webhookData = {
      url: ''
    };
    this.webhookMessage = '';
  }
}
