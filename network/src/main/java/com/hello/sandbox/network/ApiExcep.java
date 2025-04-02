package com.hello.sandbox.network;

import androidx.annotation.Nullable;
import com.hello.sandbox.network.exception.ErrorMsg;
import okhttp3.Request;
import okhttp3.Response;

public class ApiExcep extends RuntimeException {
  public Response response;

  public static String getMessage(Response response, String msg) {
    Request request = response == null ? null : response.request();
    String requestStr = request == null ? null : request.method() + " " + request.url();
    return requestStr == null ? msg : msg + " :: " + requestStr;
  }

  public ApiExcep(Response response, String msg) {
    super(getMessage(response, msg));
    this.response = response;
  }

  public static class ParseError extends ApiExcep {
    public ParseError(Response response, Exception e, String json) {
      super(response, json.replace('\n', ' ') + "\n\n\n" + e.getMessage());
    }
  }

  public static class RequestCancelled extends ApiExcep {
    public RequestCancelled(Response response) {
      super(response, "request cancelled");
    }
  }

  public static class UploadedMediaNotFound extends ApiExcep {
    public UploadedMediaNotFound(Response response) {
      super(response, "uploaded media not found");
    }
  }

  public static class ClientExpired extends ApiExcep {
    public ClientExpired(Response response) {
      super(response, "client expired");
    }
  }

  public static class Client extends ApiExcep {
    public Client(Response response, String msg) {
      super(response, msg);
    }

    public static class UnprocessableEntity extends Client {
      public UnprocessableEntity(Response response) {
        super(response, "unprocessable entity");
      }
    }

    public static class ImATeapot extends Client {
      public ImATeapot(Response response) {
        super(response, "hahahah !!!!");
      }
    }

    public static class UnsupportedMediaType extends Client {
      public UnsupportedMediaType(Response response) {
        super(response, "unsupportd media type");
      }
    }

    public static class Gone extends Client {
      public Gone(Response response) {
        super(response, "gone");
      }
    }

    public static class Conflict extends Client {
      public Conflict(Response response) {
        super(response, "conflict");
      }
    }

    public static class MethodNotAllowed extends Client {
      public MethodNotAllowed(Response response) {
        super(response, "method not allowed");
      }
    }

    public static class NotFound extends Client {
      public NotFound(Response response) {
        super(response, "not found");
      }
    }

    public static class Forbidden extends Client {
      public Forbidden(Response response) {
        super(response, "forbidden");
      }
    }

    public static class Unauthorized extends Client {
      public Unauthorized(Response response) {
        super(response, "unauthorized");
      }
    }

    public static class TooManyRequests extends Client {
      public final int resetInSeconds;

      public TooManyRequests(Response response, int resetInSeconds) {
        super(response, "too many requests. reset in " + resetInSeconds);
        this.resetInSeconds = resetInSeconds;
      }
    }

    public static class BadRequest extends Client {
      @Nullable public ErrorMsg errorMsg = null;

      public BadRequest(Response response) {
        super(response, "bad request");
      }

      public BadRequest(Response response, String msg) {
        super(response, msg);
      }
    }

    public static class RequestEntityTooLarge extends Client {
      public RequestEntityTooLarge(Response response) {
        super(response, "entity too large");
      }
    }
  }

  public static class Server extends ApiExcep {
    public Server(Response response) {
      super(response, "internal server error");
    }
  }
}
