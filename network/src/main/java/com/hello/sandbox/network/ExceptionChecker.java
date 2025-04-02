package com.hello.sandbox.network;

import com.hello.sandbox.network.ApiExcep.Client.BadRequest;
import com.hello.sandbox.network.ApiExcep.Client.Conflict;
import com.hello.sandbox.network.ApiExcep.Client.Forbidden;
import com.hello.sandbox.network.ApiExcep.Client.Gone;
import com.hello.sandbox.network.ApiExcep.Client.ImATeapot;
import com.hello.sandbox.network.ApiExcep.Client.MethodNotAllowed;
import com.hello.sandbox.network.ApiExcep.Client.NotFound;
import com.hello.sandbox.network.ApiExcep.Client.RequestEntityTooLarge;
import com.hello.sandbox.network.ApiExcep.Client.TooManyRequests;
import com.hello.sandbox.network.ApiExcep.Client.Unauthorized;
import com.hello.sandbox.network.ApiExcep.Client.UnprocessableEntity;
import com.hello.sandbox.network.ApiExcep.Client.UnsupportedMediaType;
import com.hello.sandbox.network.ApiExcep.Server;
import okhttp3.Response;

public class ExceptionChecker {

  private static final String TAG = "ExceptionChecker";

  public ExceptionChecker() {}

  public static Exception check(Response response) {
    int code = response.code();
    if (code / 100 == 2) {
      return null;
    } else if (code == 400) {
      return new BadRequest(response);
    } else if (code == 401) {
      return new Unauthorized(response);
    } else if (code == 403) {
      return new Forbidden(response);
    } else if (code == 404) {
      return new NotFound(response);
    } else if (code == 405) {
      return new MethodNotAllowed(response);
    } else if (code == 409) {
      return new Conflict(response);
    } else if (code == 410) {
      return new Gone(response);
    } else if (code == 413) {
      return new RequestEntityTooLarge(response);
    } else if (code == 415) {
      return new UnsupportedMediaType(response);
    } else if (code == 418) {
      return new ImATeapot(response);
    } else if (code == 422) {
      return new UnprocessableEntity(response);
    } else if (code == 429) {
      String resetHeader = response.header("X-RateLimit-Reset");
      if (resetHeader != null) {
        int resetInSeconds = Math.min(Integer.parseInt(resetHeader), 10);
        return new TooManyRequests(response, resetInSeconds);
      } else {
        return new TooManyRequests(response, 5);
      }
    } else {
      return (Exception) (code / 100 == 5 ? new Server(response) : new Exception("code: " + code));
    }
  }
}
