import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080/api/auth' 
    : 'https://isg-production.up.railway.app/api/auth';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  register(data: {email: string, password: string, role: string, hospitalId?: number}): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, data).pipe(
      tap((res: any) => this.setToken(res.token))
    );
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, data).pipe(
      tap((res: any) => this.setToken(res.token))
    );
  }

  patientLogin(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/patient-login`, data).pipe(
      tap((res: any) => this.setToken(res.token))
    );
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('jwt_token');
  }

  private setToken(token: string): void {
    localStorage.setItem('jwt_token', token);
    this.isAuthenticatedSubject.next(true);
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      // Basic base64 decoding for JWT payload
      const decoded = atob(payload);
      return JSON.parse(decoded).role;
    } catch (e) {
      return null;
    }
  }

  isHospital(): boolean {
    return this.getRole() === 'HOSPITAL';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}
