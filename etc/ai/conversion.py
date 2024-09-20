import torch
import torch.utils.data
import torch.utils.data

from one_train import Model

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

class Opt:
    def __init__(self):
        self.exp_name = 'TPS-ResNet-BiLSTM-Attn'
        self.train_data = 'lmdb/training'
        self.valid_data = 'lmdb/validation'
        self.select_data = 'MJ-ST'
        self.batch_ratio = '1.0-0'
        self.total_data_usage_ratio = 1.0
        self.batch_size = 16
        self.num_iter = 80000
        self.valInterval = 2000
        self.saved_model = 'trained/trained.pth'  # Update this path with your model's path
        self.FT = False
        self.adam = False
        self.lr = 1
        self.beta1 = 0.9
        self.rho = 0.95
        self.eps = 1e-8
        self.grad_clip = 5
        self.batch_max_length = 9
        self.imgH = 150
        self.imgW = 400
        self.rgb = False
        self.character = '0123456789가나다라마거너더러머어저고노도로모보오조소수구누두루무부우주바사아자하허호배육해공국합경전충남북기강원울제산인천광대전'
        self.sensitive = False
        self.PAD = False
        self.data_filtering_off = False
        self.Transformation = 'TPS'
        self.FeatureExtraction = 'ResNet'
        self.SequenceModeling = 'BiLSTM'
        self.Prediction = 'Attn'
        self.num_fiducial = 20
        self.num_class = 73
        self.input_channel = 1
        self.output_channel = 512
        self.hidden_size = 256
        self.workers = 0
        self.manualSeed = 1111

opt = Opt()

def remove_module_prefix(state_dict):
    """Helper function to remove the 'module.' prefix in DataParallel models"""
    new_state_dict = {}
    for key, value in state_dict.items():
        new_key = key.replace('module.', '')
        new_state_dict[new_key] = value
    return new_state_dict

def load_model(opt, model):
    print(f"loading pretrained model from {opt.saved_model}")

    state_dict = torch.load(opt.saved_model, map_location=torch.device('cpu'))

    state_dict = remove_module_prefix(state_dict)

    model.load_state_dict(state_dict)

    print("Model loaded successfully!")

def build_model(opt):
    model = Model(opt)
    load_model(opt, model)
    return model

model = build_model(opt)
model = model.to('cpu')

dummy_input = torch.rand(1, opt.input_channel, opt.imgH, opt.imgW)
dummy_text = torch.LongTensor(1, opt.batch_max_length + 1).fill_(0)

scripted_model = torch.jit.trace(model, (dummy_input, dummy_text))

scripted_model.save("for_cpu.pt")

print("TorchScript model saved as ocr_model_android.pt")

