package com.grupok.watertrack.fragments.mainactivityfrags.addreportview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import com.grupok.watertrack.R;
import com.grupok.watertrack.activitys.MainActivity;
import com.grupok.watertrack.database.entities.ContadorEntity;
import com.grupok.watertrack.databinding.FragmentMainACReportBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainACReportFrag extends Fragment {

    private MainActivity parent;
    private FragmentMainACReportBinding binding;
    private List<ContadorEntity> contadoresEntityList;
    private List<String> listString = new ArrayList<>();
    private int contadorId;
    private String contadorNome;
    private String contadorMorada;

    public MainACReportFrag() {
        // Required empty public constructor
    }

    public MainACReportFrag(MainActivity parent, List<ContadorEntity> contadoresEntityList) {
        this.parent = parent;
        this.contadoresEntityList = contadoresEntityList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainACReportBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            contadorId = getArguments().getInt("contadorId", -1);
        }

        if (contadoresEntityList != null && contadorId != -1) {
            for (ContadorEntity c : contadoresEntityList) {
                if (c.id == contadorId) {
                    contadorNome = c.nome;
                    contadorMorada = c.morada;
                    break;
                }
            }
        }

        if (parent != null && parent.currentUserInfo != null) {
            init();
        }

        return binding.getRoot();
    }

    private void init() {
        int cargo = parent.currentUserInfo.cargo;

        fillProblem(listString);
        setupComboProblem();
        setupComboMeter();
        setupButtonSave();
        setupUserType(cargo);
    }

    private void fillProblem(List<String> list) {
        String[] problemas = getResources().getStringArray(R.array.problem_report);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                problemas
        );

        binding.comboBoxProblemReportFragMainAc.setAdapter(adapter);

        binding.inputLayoutComboBoxProblemReportFragMainAc.setEndIconOnClickListener(
                v -> binding.comboBoxProblemReportFragMainAc.showDropDown());
        binding.comboBoxProblemReportFragMainAc.setOnClickListener(
                v -> binding.comboBoxProblemReportFragMainAc.showDropDown());
    }

    private void setupComboProblem() {
        binding.comboBoxProblemReportFragMainAc.setOnItemClickListener((adapterView, view, position, id) -> {
            String selected = listString.get(position);
            if (selected.equalsIgnoreCase("Other")) {
                binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.VISIBLE);
            } else {
                binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.GONE);
            }
        });
    }

    private void setupComboMeter() {
        if (contadoresEntityList == null || contadoresEntityList.isEmpty()) return;

        // Cria adaptador personalizado
        MeterAdapter adapter = new MeterAdapter(requireContext(), contadoresEntityList);
        binding.comboBoxMeterReportFragMainAc.setAdapter(adapter);

        // Mostrar lista ao clicar
        binding.comboBoxMeterReportFragMainAc.setOnClickListener(v -> binding.comboBoxMeterReportFragMainAc.showDropDown());
        binding.comboBoxMeterReportFragMainAc.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.comboBoxMeterReportFragMainAc.showDropDown();
        });

        // Mostrar lista novamente quando texto for apagado
        binding.comboBoxMeterReportFragMainAc.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.comboBoxMeterReportFragMainAc.showDropDown();
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void setupButtonSave() {
        binding.butSaveReportFragMainAC.setOnClickListener(v -> {
            if (binding.inputLayoutTextInputOtherProblemReportFragMainAc.getVisibility() == View.VISIBLE) {
                String novoProblema = binding.editTextOtherProblemReportFragMainAc.getText().toString().trim();
                if (!novoProblema.isEmpty() && !listString.contains(novoProblema)) {
                    listString.add(listString.size() - 1, novoProblema);
                    ((ArrayAdapter<String>) binding.comboBoxProblemReportFragMainAc.getAdapter()).notifyDataSetChanged();
                    binding.editTextOtherProblemReportFragMainAc.setText("");
                    binding.inputLayoutTextInputOtherProblemReportFragMainAc.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupUserType(int cargo) {
        if (cargo == 1) { // técnico
            if (contadorId != -1) {
                // Se veio um contador no bundle → mostra-o
                String texto = "" + contadorId;
                if (!contadorMorada.isEmpty()) texto += " — " + contadorMorada;
                binding.comboBoxMeterReportFragMainAc.setText(texto);
            } else {
                // Caso NÃO venha bundle → deixa o campo vazio, mas lista aparece igual
                binding.comboBoxMeterReportFragMainAc.setText("");
                binding.comboBoxMeterReportFragMainAc.setHint("Selecione o contador");
                // Abre lista ao focar
                binding.comboBoxMeterReportFragMainAc.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) binding.comboBoxMeterReportFragMainAc.showDropDown();
                });
            }
            Toast.makeText(requireContext(), "Técnico", Toast.LENGTH_SHORT).show();

        } else if (cargo == 0) { // morador
            if (contadorId != -1) {
                String texto = "" + contadorId;
                if (!contadorNome.isEmpty()) texto += " — " + contadorNome;
                binding.comboBoxMeterReportFragMainAc.setText(texto);
            }
            Toast.makeText(requireContext(), "Morador", Toast.LENGTH_SHORT).show();
        }
    }

    private static class MeterAdapter extends ArrayAdapter<String> implements Filterable {
        private final Context context;
        private final List<ContadorEntity> originalList;
        private List<String> filteredList;

        public MeterAdapter(Context context, List<ContadorEntity> contadores) {
            super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
            this.context = context;
            this.originalList = contadores;
            this.filteredList = buildStringList(contadores);
            addAll(filteredList);
        }

        private static List<String> buildStringList(List<ContadorEntity> list) {
            List<String> result = new ArrayList<>();
            for (ContadorEntity c : list) {
                String texto = c.id + " — " + c.nome + " (" + c.morada + ")";
                result.add(texto);
            }
            return result;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<String> filtered = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filtered = buildStringList(originalList);
                    } else {
                        String query = constraint.toString().toLowerCase(Locale.ROOT);
                        for (ContadorEntity c : originalList) {
                            if (String.valueOf(c.id).contains(query) ||
                                    c.nome.toLowerCase(Locale.ROOT).contains(query) ||
                                    c.morada.toLowerCase(Locale.ROOT).contains(query)) {
                                filtered.add(c.id + " — " + c.nome + " (" + c.morada + ")");
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filtered;
                    results.count = filtered.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    if (results != null && results.values != null) {
                        addAll((List<String>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}
