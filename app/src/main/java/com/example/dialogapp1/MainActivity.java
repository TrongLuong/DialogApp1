package com.example.dialogapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnClick;
    ArrayList<NhanVien> list;
    ArrayAdapter<NhanVien> adapters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnClick = findViewById(R.id.btnClick);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_context,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dialog:
                displayDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<NhanVien> selectAll() {
        ArrayList<NhanVien> ds = new ArrayList<>();
        Uri uri = Provider.CONTENT_URI;
        Cursor cur = getContentResolver().query(uri, null, "Select * from nhanvien", null, null);

        if (cur != null)
            cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            ds.add(new NhanVien(cur.getString(0), cur.getString(1), cur.getString(2)));
            cur.moveToNext();
        }
        cur.close();
        return ds;
    }
    public void updateNV(NhanVien nv) {

        Uri uri = Provider.CONTENT_URI;
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", nv.getName());
        contentValues.put("address", nv.getAddress());
        int kt = getContentResolver().update(uri, contentValues, nv.getId(), null);
        if (kt > 0) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

            int vt = list.indexOf(nv);
            list.get(vt).setName(nv.getName());
            list.get(vt).setAddress(nv.getAddress());
        } else
            Toast.makeText(this, "Không có nv này", Toast.LENGTH_SHORT).show();
    }
    public void deleteNV(String id) {
        if (id.equals(""))
            Toast.makeText(this, "ID không được rỗng", Toast.LENGTH_SHORT).show();
        else {
            Uri uri = Provider.CONTENT_URI;
            int xoa = getContentResolver().delete(uri, id, null);
            Log.d("vị trí: ", "" + xoa);
            if (xoa > 0) {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                NhanVien nv = new NhanVien(id);
                list.remove(nv);
                // displayGirdView();
            } else
                Toast.makeText(this, "Không có account này", Toast.LENGTH_SHORT).show();
        }
    }
    public void insert(NhanVien nv) {
        if (list.contains(nv))
            Toast.makeText(MainActivity.this, "Trùng mã", Toast.LENGTH_SHORT).show();
        else {
            Uri uri = Provider.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put("id", nv.getId());
            values.put("name", nv.getName());
            values.put("address", nv.getAddress());
            getContentResolver().insert(uri, values);
            list.add(nv);
            Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_activity);
        dialog.show();
        //khóa dialog, khi click ngoài k bị mất
       // dialog.setCancelable(false);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnSelect = dialog.findViewById(R.id.btnSelect);
        Button btnDelete = dialog.findViewById(R.id.btnDeltete);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        Button btnExit = dialog.findViewById(R.id.btnExit);

        final EditText edtId = dialog.findViewById(R.id.edtID);
        final EditText edtName = dialog.findViewById(R.id.edtName);
        final EditText edtDiachi = dialog.findViewById(R.id.edtDiaChi);

        final GridView gridView = dialog.findViewById(R.id.girdView);


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList<>();
                list = selectAll();
                for (NhanVien a : list) {
                    arrayList.add(a.getId() + "");
                    arrayList.add(a.getName());
                    arrayList.add(a.getAddress());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                gridView.setAdapter(adapter);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NhanVien nv = new NhanVien(edtId.getText().toString(), edtName.getText().toString(), edtDiachi.getText().toString());
                insert(nv);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NhanVien nv = new NhanVien(edtId.getText().toString(), edtName.getText().toString(), edtDiachi.getText().toString());
                updateNV(nv);

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNV(edtId.getText().toString());
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int rounderPos = (Math.round(position/3)) * 3;
              edtId.setText(parent.getItemAtPosition(rounderPos).toString());
            }
        });








    }

}
