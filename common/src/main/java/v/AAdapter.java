package v;

import java.util.List;

/** Created by molikto on 01/06/15. */
public abstract class AAdapter<T> extends BAdapter<T> {

  public abstract List<T> list();

  @Override
  public int getCount() {
    return list().size();
  }

  @Override
  public T getItem(int position) {
    return list().get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
}
