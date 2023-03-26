package xyz.doikki.videocontroller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videocontroller.bean.JSBean;

public class XPopAdapter extends RecyclerView.Adapter<XPopAdapter.ViewHolder> {
    public int layout;
    public List<JSBean> b;

    /* renamed from: StandardVideoController  reason: collision with root package name */
    public Context f6313c;

    /* renamed from: d  reason: collision with root package name */
    public e f6314d;

    /* renamed from: e  reason: collision with root package name */
    public OnClickListener f6315e;

    /* renamed from: f  reason: collision with root package name */
    public int index;

    /* loaded from: classes.dex */
//    public class a implements View.OnClickListener {
//        public final /* synthetic */ int a;
//
//        public a(int i2) {
//            this.a = i2;
//        }
//
//        @Override // android.view.View.OnClickListener
//        public void onClick(View view) {
//            XPopAdapter.this.f6315e.onClick(this.a);
//            XPopAdapter.this.notifyDataSetChanged();
//        }
//    }

    /* loaded from: classes.dex */
//    public class b implements View.OnLongClickListener {
//        public final /* synthetic */ int a;
//
//        public b(int i2) {
//            this.a = i2;
//        }
//
//        @Override // android.view.View.OnLongClickListener
//        public boolean onLongClick(View view) {
//            XPopAdapter.this.f6314d.a(this.a);
//            return true;
//        }
//    }

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void onClick(int i2);
    }

    /* loaded from: classes2.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(XPopAdapter xPopAdapter, View view) {
            super(view);
            textView = view.findViewById(R.id.tv_sharpnes);
        }
    }

    /* loaded from: classes.dex */
    public interface e {
        boolean a(int i2);
    }

    public XPopAdapter(int lay, List<JSBean> list, Context context) {
        this.layout = layout;
        this.b = list;
        this.f6313c = context;
    }

    public int getIndex() {
        return this.index;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: d */
    public void onBindViewHolder(@NonNull ViewHolder dVar, @SuppressLint({"RecyclerView"}) int i2) {
        String title;
        if (this.b.get(i2).getTitle().isEmpty()) {
            if (this.b.size() > 2) {
                title = "第" + (dVar.getBindingAdapterPosition() + 1) + "集";
            } else {
                title = (dVar.getBindingAdapterPosition() + 1) + "";
            }
        } else {
            title = this.b.get(i2).getTitle();
        }
        dVar.textView.setText(title);
        dVar.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f6315e.onClick(i2);
                notifyDataSetChanged();
            }
        });
        if (i2 == getIndex()) {
            dVar.textView.setTextColor(Color.parseColor("#F98B56"));
        } else {
            dVar.textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if (this.f6314d != null) {
            dVar.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    f6314d.a(i2);
                    return true;
                }
            });
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    /* renamed from: e */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(this, LayoutInflater.from(viewGroup.getContext()).inflate(this.layout, (ViewGroup) null));
    }

    public void setOnClickListener(OnClickListener cVar) {
        this.f6315e = cVar;
    }

    public void setIndex(int i2) {
        this.index = i2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.b.size();
    }
}

