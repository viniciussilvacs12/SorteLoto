import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req,next) => {
  const raw=localStorage.getItem('sorteloto_user') || localStorage.getItem('smartloto_user');
  if(!raw) return next(req);

  try{
    const user=JSON.parse(raw);
    if(!user?.token) return next(req);
    return next(req.clone({
      setHeaders:{Authorization:`Bearer ${user.token}`}
    }));
  }catch{
    return next(req);
  }
};
