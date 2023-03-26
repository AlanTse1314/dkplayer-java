package xyz.doikki.videoplayer.videocontroller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import xyz.doikki.videoplayer.R;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    public String[] a;
    public Context b;

    /* renamed from: StandardVideoController  reason: collision with root package name */
    public int f6306c;

    /* renamed from: d  reason: collision with root package name */
    public OnClickListener f6307d;  //public c f6307d;

    /* renamed from: e  reason: collision with root package name */
    public int f6308e;

    /* renamed from: f  reason: collision with root package name */
    public e f6309f;

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
//            TagAdapter.this.f6307d.onClick(this.a);
//            TagAdapter.this.notifyDataSetChanged();
//        }
//    }
//
//    /* loaded from: classes.dex */
//    public class b implements View.OnLongClickListener {
//        public final /* synthetic */ int a;
//
//        public b(int i2) {
//            this.a = i2;
//        }
//
//        @Override // android.view.View.OnLongClickListener
//        public boolean onLongClick(View view) {
//            TagAdapter.this.f6309f.a(view, this.a);
//            return true;
//        }
//    }

    /* loaded from: classes.dex */
    public interface OnClickListener {
        void onClick(int i2);
    }

    /* loaded from: classes2.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView a;

        public ViewHolder(TagAdapter tagAdapter, View view) {
            super(view);
            this.a = (TextView) view.findViewById(R.id.tv_tag);
        }
    }

    /* loaded from: classes.dex */
    public interface e {
        void a(View view, int i2);
    }

    public TagAdapter(int i2, String[] strArr, Context context) {
        this.a = strArr;
        this.b = context;
        this.f6306c = i2;
    }

    public int b() {
        return this.f6308e;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: StandardVideoController */
    public void onBindViewHolder(@NonNull ViewHolder dVar, @SuppressLint({"RecyclerView"}) int i2) {
        dVar.a.setText(this.a[i2]);
        dVar.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f6307d.onClick(i2);
                notifyDataSetChanged();
            }
        });
        dVar.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                f6309f.a(v, i2);
                return true;
            }
        });
        if (i2 == b()) {
            dVar.a.setTextColor(Color.parseColor("#F37B68"));
        } else {
            dVar.a.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    /* renamed from: d */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i2) {
        return new ViewHolder(this, LayoutInflater.from(this.b).inflate(this.f6306c, (ViewGroup) null));
    }

    public void setOnClickListener(OnClickListener cVar) {
        this.f6307d = cVar;
    }

    public void f(int i2) {
        this.f6308e = i2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.a.length;
    }
}

