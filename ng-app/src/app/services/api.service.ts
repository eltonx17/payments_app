import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentData {
  firstName: string;
  lastName: string;
  zipCode: string;
  cardNumber: string;
  requestId: string;
}

export interface WebhookData {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = ''; // Will be set to <host> when needed
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'X-API-KEY': 'test-api-key'
    })
  };

  constructor(private http: HttpClient) {}

  createPayment(paymentData: PaymentData): Observable<any> {
    if (!this.baseUrl) {
      throw new Error('API base URL not configured. Please set the API host first.');
    }
    const url = `${this.baseUrl}/v1/payments/create`;
    console.log('Making payment API call to:', url, 'with data:', paymentData);
    return this.http.post(url, paymentData, this.httpOptions);
  }

  createWebhook(webhookData: WebhookData): Observable<any> {
    if (!this.baseUrl) {
      throw new Error('API base URL not configured. Please set the API host first.');
    }
    const url = `${this.baseUrl}/v1/webhooks/create`;
    console.log('Making webhook API call to:', url, 'with data:', webhookData);
    return this.http.post(url, webhookData, this.httpOptions);
  }

  setBaseUrl(host: string) {
    this.baseUrl = host;
  }
}
