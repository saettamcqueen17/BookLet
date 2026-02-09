import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthStateService {

  private _logged = new BehaviorSubject<boolean>(false);
  readonly logged$ = this._logged.asObservable();

  setLogged(v: boolean) {
    this._logged.next(v);
  }

  get logged(): boolean {
    return this._logged.value;
  }
}
