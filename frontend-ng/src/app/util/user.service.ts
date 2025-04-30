import { Injectable } from '@angular/core';
import {ApiService} from '../api/services/api.service';
import {UserDto} from '../api/models/user-dto';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthorityEnumDto} from '../api/models/authority-enum-dto';

export enum AuthorityStatus {
	HasIt, DoesNotHave, NotLoggedIn, Idk
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(
	  private api: ApiService
  ) {}

  lastUser?: UserDto;
  pendingPromise?: Promise<UserDto | undefined>;

  invalidateCachedUser() {
	  this.lastUser = undefined;
  }

  hasAuthority(auth: AuthorityEnumDto): Promise<AuthorityStatus> {
	  return this.getUser().then(v => v == undefined
			  ? AuthorityStatus.NotLoggedIn
			  : (v.authorities.includes(auth) ? AuthorityStatus.HasIt : AuthorityStatus.DoesNotHave),
	  		_ => AuthorityStatus.Idk)
  }

  getUser(): Promise<UserDto | undefined> {
	  if (!this.pendingPromise && !this.lastUser) {
		  // refresh user
		  return this.pendingPromise = new Promise<UserDto | undefined>((res, rej) => {
			  this.api.getLoggedInUser()
				  .subscribe({
					  next: u => {
						  this.lastUser = u
						  res(u)
						  this.pendingPromise = undefined;
					  },
					  error: e => {
						  if (e instanceof HttpErrorResponse && e.status == 401) {
							  this.lastUser = undefined
							  res(undefined)
						  }
						  else {
							  console.error("failed fetching user", e)
							  rej(e)
						  }
						  this.pendingPromise = undefined;
					  }
				  })
		  })
	  } else if (this.pendingPromise) { return this.pendingPromise; } else {
		  // use cached
		  return new Promise<UserDto | undefined>((res) => {
			  res(this.lastUser)
		  })
	  }
  }
}
